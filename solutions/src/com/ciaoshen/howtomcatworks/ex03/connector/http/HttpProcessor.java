package com.ciaoshen.howtomcatworks.ex03.connector.http;

import com.ciaoshen.howtomcatworks.ex03.ServletProcessor;
import com.ciaoshen.howtomcatworks.ex03.StaticResourceProcessor;

import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.StringManager;


/********************************************************************
 * this class used to be called HttpServer
 * connecter/http/ 包下两大主要控件：
 *      HttpConnector: 主管接受客户端TCP连接请求
 *      HttpProcessor: 主管解析Http消息
 *
 * 进一步细分，HttpProcessor主要做两件事：
 *      1. 解析请求行
 *          利用现成的SocketInputStream#readRequestLine()函数
 *          解析后拿到的是HttpRequestLine实例，逐域赋值给HttpRequest实例
 *      2. 解析请求头
 *          利用现成的SocketInputStream#readHeader()方法
 *          解析后的信息以键-值对的形式储存在org.apache.catalina.util.ParameterMap里
 *******************************************************************/
public class HttpProcessor {

  public HttpProcessor(HttpConnector connector) {
    this.connector = connector;
  }
  /**
   * The HttpConnector with which this processor is associated.
   */
  private HttpConnector connector = null;
  private HttpRequest request;
  private HttpRequestLine requestLine = new HttpRequestLine();
  private HttpResponse response;

  protected String method = null;
  protected String queryString = null;

  /**
   * The string manager for this package.
   */
  protected StringManager sm =
    StringManager.getManager(Constants.PACK_HTTP);

  public void process(Socket socket) {

    /**
     * 利用com.ciaoshen.howtomcatworks.ex03.connector.http.SocketInputStream类
     * 的readRequestLine()和readHeader()方法分别解析Http请求行和消息头
     *
     * 特别注意，这个SocketInputStream不是org.appache.commons.net.io.SocketInputStream
     * 而是org.appache.catalina.connector.http.SocketInputStream，但确实是它的一个本地副本
     */
    SocketInputStream input = null;
    OutputStream output = null;
    try {
      input = new SocketInputStream(socket.getInputStream(), 2048);
      output = socket.getOutputStream();

      // create HttpRequest object and parse
      request = new HttpRequest(input);

      // create HttpResponse object
      response = new HttpResponse(output);
      response.setRequest(request);
      response.setHeader("Server", "SHEN Servlet Container");

      /**
       * 此时request字段指向一个HttpRequest实例,
       * 虽然parseRequest()函数没有显式地将HttpRequest实例作为参数,
       * 但可以把解析出来内容赋值给环境里的HttpRequest对象.
       */
      parseRequest(input, output);
      parseHeaders(input);

      //check if this is a request for a servlet or a static resource
      //a request for a servlet begins with "/servlet/"
      if (request.getRequestURI().startsWith("/servlet/")) {
        ServletProcessor processor = new ServletProcessor();
        processor.process(request, response);
      }
      else {
        StaticResourceProcessor processor = new StaticResourceProcessor();
        processor.process(request, response);
      }

      // Close the socket
      socket.close();
      // no shutdown for this application
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is the simplified version of the similar method in
   * org.apache.catalina.connector.http.HttpProcessor.
   * However, this method only parses some "easy" headers, such as
   * "cookie", "content-length", and "content-type", and ignore other headers.
   * @param input The input stream connected to our socket
   *
   * @exception IOException if an input/output error occurs
   * @exception ServletException if a parsing error occurs
   *
   * Http Header “Http头” 就是在第一行 "请求头" 后面，直到空行和消息正文前的部分。比如，
   *    Server: Microsoft-IIS/4.0
   *    Date: Mon, 5 Jan 2004 13:13:12 GMT
   *    Content-Type: text/html
   *    Last-Modified: Mon, 5 Jan 2004 13:13:12 GMT
   *    Content-Length: 112
   *
   * parseHeaders()函数就是调用现成的轮子SocketInputStream#readHeader()方法解析Http头
   */
  private void parseHeaders(SocketInputStream input)
    throws IOException, ServletException {

    /**
     * 这里要不断循环读取，因为Http头有很多项属性，每一行是一个属性
     * SocketInputStream#readHeader()函数每次只读一行属性。返回一个[name,value]键值对，
     * 比如上面例子中的第一行属性 -> "Server: Microsoft-IIS/4.0"，解析出来，
     *      name = "Server"
     *      value = "Microsoft-IIS/4.0"
     */
    while (true) {
      HttpHeader header = new HttpHeader();;

      /**
       * 调用现成的轮子SocketInputStream#readHeader()方法解析Http消息请求行后面的Http消息头
       * 这里的header参数是HttpHeader类的实例
       * 解析的结果会被用来填充一系列的HttpHeader实例。
       */
      input.readHeader(header);
      if (header.nameEnd == 0) {
        if (header.valueEnd == 0) {
          return;
        }
        else {
          throw new ServletException
            (sm.getString("httpProcessor.parseHeaders.colon"));
        }
      }

      String name = new String(header.name, 0, header.nameEnd);
      String value = new String(header.value, 0, header.valueEnd);
      request.addHeader(name, value);
      // do something for some headers, ignore others.
      if (name.equals("cookie")) {
        Cookie cookies[] = RequestUtil.parseCookieHeader(value);
        for (int i = 0; i < cookies.length; i++) {
          if (cookies[i].getName().equals("jsessionid")) {
            // Override anything requested in the URL
            if (!request.isRequestedSessionIdFromCookie()) {
              // Accept only the first session id cookie
              request.setRequestedSessionId(cookies[i].getValue());
              request.setRequestedSessionCookie(true);
              request.setRequestedSessionURL(false);
            }
          }
          request.addCookie(cookies[i]);
        }
      }
      else if (name.equals("content-length")) {
        int n = -1;
        try {
          n = Integer.parseInt(value);
        }
        catch (Exception e) {
          throw new ServletException(sm.getString("httpProcessor.parseHeaders.contentLength"));
        }
        request.setContentLength(n);
      }
      else if (name.equals("content-type")) {
        request.setContentType(value);
      }
    } //end while
  }


  /********************************************************************
   * 这个方法负责解析Http请求第一行的 “请求行”。
   * 一个 “请求行” 是一个Http消息的第一行：
   *    GET /servlet/ModernServlet?userName=tarzan&password=pwd HTTP/1.1
   *
   * 基本可以分为3部分：
   *    1. method:              GET
   *    2. URI(包含查询字符串):   servlet/ModernServlet?userName=tarzan&password=pwd
   *    3. protocol:            HTTP
   *******************************************************************/
  private void parseRequest(SocketInputStream input, OutputStream output)
    throws IOException, ServletException {

    /**
     * 调用现成的轮子
     * org.appache.catalina.connector.http.SocketInputStream
     * 的readRequestLine()解析 "请求行"
     * 用解析出来的数据段填充HttpRequestLine实例
     *
     * 严依据协议标准的解析Http消息，细节很繁琐，尽量不要自己再造轮子
     * SocketInputStream把第一行 “请求行” 切成3段：
     *    1. method:              GET
     *    2. URI(包含查询字符串):   servlet/ModernServlet?userName=tarzan&password=pwd
     *    3. protocol:            HTTPd
     */
    input.readRequestLine(requestLine);

    /*
     * 然后再将解析后的HttpRequestLine数据逐个赋值给HttpRequest对象
     *
     * 因为Servlet的主入口函数service(ServletRequest request, ServletResponse response)方法
     * 面向ServletRequest和ServletResponse两个接口
     *
     * 现成轮子SocketInputStream#readRequestLine()解析完Http消息请求行拿到的
     * 是HttpRequestLine实例，不是ServletRequest的实例
     * 所以需要转换一下
     */

    // method就是请求行开头的："GET;
    String method   = new String(requestLine.method, 0, requestLine.methodEnd);
    String uri      = null;
    String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);

    // System.out.println("Protocol = \"" + protocol + "\"");
    // Validate the incoming request line
    if (method.length() < 1) {
      throw new ServletException("Missing HTTP request method");
    } else if (requestLine.uriEnd < 1) {
      throw new ServletException("Missing HTTP request URI");
    }

    // Parse any query parameters out of the request URI
    /**
     * 把原本一整个URI：
     *      /servlet/ModernServlet?userName=tarzan&password=pwd
     * 以"?"为界切成两段，
     *      uri："servlet/ModernServlet"
     *      queryString: "userName=tarzan&password=pwd"
     *
     * 注意！queryString就解析到这样
     * 至于下一步解析成[userName=tarzan, password=pwd]
     * 要到HttpRequest#parseParameters()函数里再分解。
     */
    int question = requestLine.indexOf("?");
    if (question >= 0) {
      request.setQueryString(new String(requestLine.uri, question + 1,
        requestLine.uriEnd - question - 1));
      uri = new String(requestLine.uri, 0, question);
    }
    else {
      request.setQueryString(null);
      uri = new String(requestLine.uri, 0, requestLine.uriEnd);
    }


    // Checking for an absolute URI (with the HTTP protocol)
    /**
     * 大部分URI都指向一个相对路径中的资源，比如我们会用的，
     *      /servlet/ModernServlet?userName=tarzan&password=pwd
     * 当然URI也可以是一个绝对路径：
     *      http://www.brainysoftware.com/index.html?name=Tarzan
     * 我们这里不属于这种情况。
     */
    if (!uri.startsWith("/")) {
      int pos = uri.indexOf("://");
      // Parsing out protocol and host name
      if (pos != -1) {
        pos = uri.indexOf('/', pos + 3);
        if (pos == -1) {
          uri = "";
        }
        else {
          uri = uri.substring(pos);
        }
      }
    }

    // Parse any requested session ID out of the request URI
    /**
     * 查询字符串可能会有一个参数"jsessionid"，标明会话标识符。
     * 这里的例子里没有这个参数。
     */
    String match = ";jsessionid=";
    int semicolon = uri.indexOf(match);
    if (semicolon >= 0) {
      String rest = uri.substring(semicolon + match.length());
      int semicolon2 = rest.indexOf(';');
      if (semicolon2 >= 0) {
        request.setRequestedSessionId(rest.substring(0, semicolon2));
        rest = rest.substring(semicolon2);
      }
      else {
        request.setRequestedSessionId(rest);
        rest = "";
      }
      request.setRequestedSessionURL(true);
      uri = uri.substring(0, semicolon) + rest;
    }
    else {
      request.setRequestedSessionId(null);
      request.setRequestedSessionURL(false);
    }

    // Normalize URI (using String operations at the moment)
    /** 例子里传入的URI是标准的，normalize()不改变传入的URI。 **/
    String normalizedUri = normalize(uri);

    // Set the corresponding request properties
    ((HttpRequest) request).setMethod(method);
    request.setProtocol(protocol);
    if (normalizedUri != null) {
      ((HttpRequest) request).setRequestURI(normalizedUri);
    }
    else {
      ((HttpRequest) request).setRequestURI(uri);
    }

    if (normalizedUri == null) {
      throw new ServletException("Invalid URI: " + uri + "'");
    }
  }

  /**
   * Return a context-relative path, beginning with a "/", that represents
   * the canonical version of the specified path after ".." and "." elements
   * are resolved out.  If the specified path attempts to go outside the
   * boundaries of the current context (i.e. too many ".." path elements
   * are present), return <code>null</code> instead.
   *
   * @param path Path to be normalized
   */
  protected String normalize(String path) {
    if (path == null)
      return null;
    // Create a place for the normalized path
    String normalized = path;

    // Normalize "/%7E" and "/%7e" at the beginning to "/~"
    if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
      normalized = "/~" + normalized.substring(4);

    // Prevent encoding '%', '/', '.' and '\', which are special reserved
    // characters
    if ((normalized.indexOf("%25") >= 0)
      || (normalized.indexOf("%2F") >= 0)
      || (normalized.indexOf("%2E") >= 0)
      || (normalized.indexOf("%5C") >= 0)
      || (normalized.indexOf("%2f") >= 0)
      || (normalized.indexOf("%2e") >= 0)
      || (normalized.indexOf("%5c") >= 0)) {
      return null;
    }

    if (normalized.equals("/."))
      return "/";

    // Normalize the slashes and add leading slash if necessary
    if (normalized.indexOf('\\') >= 0)
      normalized = normalized.replace('\\', '/');
    if (!normalized.startsWith("/"))
      normalized = "/" + normalized;

    // Resolve occurrences of "//" in the normalized path
    while (true) {
      int index = normalized.indexOf("//");
      if (index < 0)
        break;
      normalized = normalized.substring(0, index) +
        normalized.substring(index + 1);
    }

    // Resolve occurrences of "/./" in the normalized path
    while (true) {
      int index = normalized.indexOf("/./");
      if (index < 0)
        break;
      normalized = normalized.substring(0, index) +
        normalized.substring(index + 2);
    }

    // Resolve occurrences of "/../" in the normalized path
    while (true) {
      int index = normalized.indexOf("/../");
      if (index < 0)
        break;
      if (index == 0)
        return (null);  // Trying to go outside our context
      int index2 = normalized.lastIndexOf('/', index - 1);
      normalized = normalized.substring(0, index2) +
        normalized.substring(index + 3);
    }

    // Declare occurrences of "/..." (three or more dots) to be invalid
    // (on some Windows platforms this walks the directory tree!!!)
    if (normalized.indexOf("/...") >= 0)
      return (null);

    // Return the normalized path that we have completed
    return (normalized);

  }

}

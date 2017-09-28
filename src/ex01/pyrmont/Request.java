package ex01.pyrmont;

import java.io.InputStream;
import java.io.IOException;

/**
 * Request类用来封装从客户端发来的请求。
 */
public class Request {

  private InputStream input;
  private String uri;

  /**
   * 构造函数的参数是ServerSocket的输入流。
   * 从输入流接收客户端发起的请求。
   */
  public Request(InputStream input) {
    this.input = input;
  }

  public void parse() {
    // Read a set of characters from the socket
    StringBuffer request = new StringBuffer(2048);
    int i;
    byte[] buffer = new byte[2048];
    try {
      /**
       * read(byte[] b)
       * Reads some number of bytes from the input stream and stores them into the buffer array b.
       */
      i = input.read(buffer);
    }
    catch (IOException e) {
      e.printStackTrace();
      i = -1;
    }
    for (int j=0; j<i; j++) {
      request.append((char) buffer[j]);
    }
    /**
     * 打印客户请求，并解析请求中的URI
     */
    System.out.print(request.toString());
    uri = parseUri(request.toString());
  }

  /**
   * URI前后分别有一个空格：" uri "
   * parseUri()就是从读出来的客户端请求中，搜索URI的这种模式。
   */
  private String parseUri(String requestString) {
    int index1, index2;
    index1 = requestString.indexOf(' ');
    if (index1 != -1) {
      index2 = requestString.indexOf(' ', index1 + 1);
      if (index2 > index1)
        return requestString.substring(index1 + 1, index2);
    }
    return null;
  }

  public String getUri() {
    return uri;
  }

}

package com.ciaoshen.howtomcatworks.ex02;

import javax.servlet.*;
// import java.io.IOException;
// import java.io.PrintWriter;
import com.ciaoshen.howtomcatworks.ex02.Request;
import com.ciaoshen.howtomcatworks.ex02.Response;
import java.io.*;
import java.net.URLDecoder;

/*****************************************************************************
 * 小Demo里不放太复杂的业务逻辑，只是简单地解析HTML表单通过GET方法传递过来的请求消息，
 * 并返回响应的资源。
 *
 * 假设我们服务器提供 《王者荣耀》游戏英雄属性资料的查询，通过解析GET方法传过来的英雄名字，
 * 返回相应英雄资料的图片。
 *
 * 这里有两个关键点：
 *      第一，浏览器发过来的消息用UTF-8编码过了，需要用URLDecoder.decode()方法解码
 *      第二，因为ServletRequest和ServletResponse拿不到面向字节流的I/O
 *          所以又转型回Resquest和Response类。不提倡这样做。用两个代理类，
 *          RequestFacade和ResponseFacade类可以禁止这样的操作。
 ****************************************************************************/

class KingOfGloryServlet implements Servlet {

  public void init(ServletConfig config) throws ServletException {
    System.out.println("init");
  }

  private static final int BUFFER_SIZE = 1024;
  private static byte[] bytes = new byte[BUFFER_SIZE];

  public void service(ServletRequest request, ServletResponse response)
    throws ServletException, IOException {
    FileInputStream fis = null;
    try {
        // 为了拿到uri，必须转型回Request
        String uri = ((Request)request).getUri();
        // System.out.println("Uri = " + uri);
        String fileNameInUtf8 = uri.substring(uri.indexOf("=")+1);
        /**
         * 把浏览器传过来的 %E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96 转换回 “百里玄策”
         * 浏览器用的是UTF-8编码，每个中文3个字节，
         *   百 = E7 99 BE
         *   里 = E9 87 8C
         *   玄 = E7 8E 84
         *   策 = E7 AD 96
         */
        String fileName = URLDecoder.decode(fileNameInUtf8,"UTF-8") + ".jpg";
        // System.out.println("Request file " + fileName);
        File file = new File(Constants.WEB_ROOT + File.separator + "images",fileName);
        // System.out.println("Full file path = " + file.getPath());
        fis = new FileInputStream(file);

        // 回应消息加HTTP头
        String httpHeader = "HTTP/1.1 200 OK\r\n\r\n"; // 最简单的HTTP消息头，只有状态行

        // ServletResponse不提供面向字节流的I/O，必须重新转型回Response
        ((Response)response).output.write(httpHeader.getBytes());
        int ch = fis.read(bytes,0,BUFFER_SIZE);
        while (ch != -1) {
            // System.out.println("Writing " + ch + " characters...");
            ((Response)response).output.write(bytes,0,ch);
            ch = fis.read(bytes,0,BUFFER_SIZE);
        }
    } catch (FileNotFoundException e) {
      String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
        "Content-Type: text/html\r\n" +
        "Content-Length: 23\r\n" +
        "\r\n" +
        "<h1>File Not Found</h1>";
      ((Response)response).output.write(errorMessage.getBytes());
    } finally {
      if (fis!=null)
        fis.close();
    }
  }

  public void destroy() {
    System.out.println("destroy");
  }

  public String getServletInfo() {
    return null;
  }
  public ServletConfig getServletConfig() {
    return null;
  }

}

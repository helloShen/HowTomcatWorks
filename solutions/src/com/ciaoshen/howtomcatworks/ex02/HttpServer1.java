package com.ciaoshen.howtomcatworks.ex02;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;


/*************************************************************************
 * 主入口
 * await()函数构造ServerSocket实例
 * 利用ServerSocket#accept()函数和客户端建立连接。拿到Socket类实例的的引用。
 * 从Socket实例再拿到一对InputStream和OutputStream
 * 分别封装到Request类和Response类里。
 * 根据任务类型分发任务，是请求静态资源，还是请求Servlet服务？
 ************************************************************************/
public class HttpServer1 {

  /**
   * WEB_ROOT常量被收拢到了Constants类。
   */

  // shutdown command
  private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

  // the shutdown command received
  private boolean shutdown = false;

  public static void main(String[] args) {
    HttpServer1 server = new HttpServer1();
    server.await();
  }

  public void await() {
    /** await()函数构造ServletSocket实例 */
    ServerSocket serverSocket = null;
    int port = 8080;
    try {
      serverSocket =  new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
    }
    catch (IOException e) {
        e.printStackTrace();
        System.exit(1);
    }

    // Loop waiting for a request
    while (!shutdown) {
      /** 利用ServerSocket#accept()函数和客户端建立连接。拿到Socket类实例的的引用。*/
      Socket socket = null;

      InputStream input = null;
      OutputStream output = null;

      try {
        System.out.println("HttpServer1 is waiting for client requests ...");
        socket = serverSocket.accept();

        /** 从Socket实例再拿到一对InputStream和OutputStream。*/
        input = socket.getInputStream();
        output = socket.getOutputStream();

        // create Request object and parse
        /* 分别封装到Request类和Response类里。*/
        Request request = new Request(input);
        request.parse();
        // create Response object
        Response response = new Response(output);
        response.setRequest(request);

        // check if this is a request for a servlet or a static resource
        /**
         * 根据任务类型分发任务，是请求静态资源，还是请求Servlet服务？
         * 我搜索“百里玄策”，收到的http请求如下，
         *      GET /index.html?king-of-glory-hero=%E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96
         */
        if (request.getUri().contains("?king-of-glory-hero=")) {
          ServletProcessor1 processor = new ServletProcessor1();
          processor.process(request, response);
        } else { // 和第一章一样，请求静态资源
          StaticResourceProcessor processor = new StaticResourceProcessor();
          processor.process(request, response);
        }

        // Close the socket
        socket.close();
        //check if the previous URI is a shutdown command
        shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
      }
      catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}

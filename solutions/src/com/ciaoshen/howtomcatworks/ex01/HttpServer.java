package com.ciaoshen.howtomcatworks.ex01;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

/**
 * HttpServer是服务端程序。
 * 不需要写客户端程序，只需要在浏览器中输入相应地址，就可以向服务器发送请求。比如，
 *      http://localhost:8080/index.html
 */
public class HttpServer {

  /*******************************************************************************
   * WEB_ROOT is the directory where our HTML and other files reside.
   *  For this package, WEB_ROOT is the "webroot" directory under the working
   *  directory.
   *  The working directory is the location in the file system
   *  from where the java command was invoked.
   *
   *
   * 注意！书上这个user.dir找不到webroot的。
   * 因为user.dir指向当前java程序执行的位置，不是用户的根目录
   *     public static final String WEB_ROOT =
   *     System.getProperty("user.dir") + File.separator  + "webroot";
   ******************************************************************************/

  /* 下面这个是我本地webroot的位置 */
  public static final String WEB_ROOT =
    System.getProperty("user.home") +
    File.separator  + "github" +
    File.separator + "howtomcatworks" +
    File.separator + "webroot";


  // shutdown command
  private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

  // the shutdown command received
  private boolean shutdown = false;

  public static void main(String[] args) {
    HttpServer server = new HttpServer();
    System.out.println("HttpServer is running! Waiting for client request...");
    server.await();
  }

  /*******************************************************************************
   * await()函数是核心。它做两件事：
   *     1. 构造ServerSocket实例
   *     2. 调用ServerSocket#accept()函数监听已建立的TCP连接发来的消息。
   ******************************************************************************/
  public void await() {
    ServerSocket serverSocket = null;
    int port = 8080;
    try {
      /***************************************************************************
       *  ServerSocket的构造函数:
       *  ServerSocket(int port, int backlog, InetAddress bindAddr)
       *  Create a serve r with the specified port, listen backlog, and local IP address to bind to.
       *  其中，第二个参数backlog是调用listen()系统调用需要的参数。
       *  它指：内核维护的两个队列：未完成连接队列(SYN_RCVD状态)和已完成连接队列(ESTABLISHED状态)，元素之和不超过backlog的上限
       *  另外，源自Berkeley的实现给backlog增设了一个模糊因子(fudge factor): 1.5。
       *  如果backlog = 8, 最大可以有8个未处理连接排队。
       **************************************************************************/
      serverSocket =  new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // Loop waiting for a request
    while (!shutdown) {
      Socket socket = null;
      InputStream input = null;
      OutputStream output = null;
      try {
        /**
         * accept()函数从已完成连接队列队头返回下一个已完成的连接。
         * 注意！如果已完成连接队列为空，那么进程将被投入睡眠（假定套接字为默认的阻塞模式）。
         * 另外，返回的socket是一个已连接套接字（connected socket）的描述符。
         */
        socket = serverSocket.accept(); // 阻塞，若没有客户端的连接请求。
        input = socket.getInputStream();
        output = socket.getOutputStream();

        // create Request object and parse
        /**
         * 从已连接套接字的输入流读取并打印客户请求，并试图解析客户请求中的URI。
         */
        Request request = new Request(input);
        request.parse();

        // create Response object
        Response response = new Response(output);
        response.setRequest(request);
        // response.sendStaticResource();
        response.sendStaticResourceSimplest(); // 最简单的只有状态行，没有响应头的响应消息

        // Close the socket
        socket.close();

        //check if the previous URI is a shutdown command
        shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
      }
      catch (Exception e) {
        e.printStackTrace();
        continue;
      }
    }
  }
}

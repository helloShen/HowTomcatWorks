package com.ciaoshen.howtomcatworks.ex03.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/*********************************************************************
 * connecter/http/ 包下两大主要控件：
 *      HttpConnector: 主管接受客户端TCP连接请求
 *      HttpProcessor: 主管解析Http消息
 *
 * 注意! HttpConnector拥有自己的独立线程
 ********************************************************************/
public class HttpConnector implements Runnable {

  boolean stopped;
  private String scheme = "http";

  public String getScheme() {
    return scheme;
  }

  public void run() {
    ServerSocket serverSocket = null;
    int port = 8080;
    try {
      serverSocket =  new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    while (!stopped) {
      // Accept the next incoming connection from the server socket
      Socket socket = null;
      try {
        /** 主要工作就在这里，接受用户连接请求，创建已连接套接字 */
        socket = serverSocket.accept();
      }
      catch (Exception e) {
        continue;
      }
      // Hand this socket off to an HttpProcessor
      HttpProcessor processor = new HttpProcessor(this);
      processor.process(socket);
    }
  }

  /** HttpConnector拥有自己独立的线程 */
  public void start() {
    Thread thread = new Thread(this);
    thread.start();
  }
}

package com.ciaoshen.howtomcatworks.ex01;

import java.io.InputStream;
import java.io.IOException;

/*******************************************************
 * Request类用来解析并封装从客户端请求中的目标资源。
 * 这个例子中用户浏览器会发送两次请求：
 *     1. 第一次请求 "./index.html"
 *     2. 第二次请求HTML页面中的图片资源 "./images/logo.gif"
 ******************************************************/
class Request { // 仅包内可见，不要影响下一章的练习

  private InputStream input;
  private String uri;

  /***********************************************
   * 构造函数的参数是ServerSocket的输入流。
   * 从输入流接收客户端发起的请求。
   **********************************************/
  public Request(InputStream input) {
    this.input = input;
  }

  /***********************************************
   * 从HTTP请求消息中解析出用户请求的资源
   **********************************************/
  public void parse() {
    // Read a set of characters from the socket
    StringBuffer request = new StringBuffer(2048);
    int i;
    byte[] buffer = new byte[2048];
    try {
      /****************************************************
       * read(byte[] b)函数
       * Reads some number of bytes from the input stream
       * and stores them into the buffer array b.
       ***************************************************/
      i = input.read(buffer);
    }
    catch (IOException e) {
      e.printStackTrace();
      i = -1;
    }
    for (int j=0; j<i; j++) {
      request.append((char) buffer[j]);
    }
    /****************************************************
     * 打印客户请求，并解析请求中的URI
     ***************************************************/
    System.out.print(request.toString());
    uri = parseUri(request.toString());
  }

  /******************************************************************************
   * 下面是从浏览器接收到的HTTP请求：
   *     GET /index.html HTTP/1.1
   *     Host: localhost:8080
   *     Upgrade-Insecure-Requests: 1
   *     UAccept: text/html,application/xhtml+xml,application/xml;q=0.9;q=0.8
   *     UUser-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/603.3.8 (KHTML, like Gecko) Version/10.1.2 Safari/603.3.8
   *     UAccept-Language: fr-ca
   *     UAccept-Encoding: gzip, deflate
   *     UConnection: keep-alive
   *
   * parseUri()函数用来解析第一行 "GET /index.html HTTP/1.1" 中的 "/index.html"
   *****************************************************************************/
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

  /****************************************************
   * POJO访问函数访问成员字段
   ***************************************************/
  public String getUri() {
    return uri;
  }

}

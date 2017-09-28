package com.ciaoshen.howtomcatworks.ex01;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.util.*;

/*
  HTTP Response = Status-Line
    *(( general-header | response-header | entity-header ) CRLF)
    CRLF
    [ message-body ]
    Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
*/

class Response { // 仅包内可见，不要影响下一章的练习

  private static final int TOTAL_SIZE = 65536;
  private static final int BUFFER_SIZE = 1024;
  Request request;
  OutputStream output;
  /**
   * 绑定服务端已绑定套接字的输出流。
   */
  public Response(OutputStream output) {
    this.output = output;
  }

  public void setRequest(Request request) {
    this.request = request;
  }

    /*********************************************************
     * 一个最简单的
     ********************************************************/
    public void sendStaticResourceSimplest() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            /*********************************************************
            * 两个参数组成客户请求资源的路径：
            *     @param String parent   [客户请求的静态资源存放的根目录]
            *     @param String child   [客户请求的静态资源的名字]
            ********************************************************/
            File file = new File(HttpServer.WEB_ROOT, request.getUri());

            if (file.exists()) {

                // 控制台报告用户正在申请的资源
                System.out.println("Client is asking for " + file.getName());
                fis = new FileInputStream(file);


                /*********************************************************
                * 必须加HTTP头，否则浏览器会因为默认HTTP/0.9而无法工作
                * HTTP头分为三部分：
                *    1. 状态行：协议-状态码-描述（必须）（比如：HTTP/1.1 200 OK）
                *    2. 请求头/响应头（可选）
                *    一个空行(必须)
                *    3. 消息正文（必须）
                * 这里最简单响应消息是：只有状态行，可以没有后面的附加响应头信息。
                ********************************************************/

                String httpHeader = "HTTP/1.1 200 OK\r\n" +

                /*********************************************************
                * 可选的响应头（可以没有）：
                * "Server: SHEN's First Java HttpServerrn" +
                * "Content-Type: text/html\r\n" +
                * "Content-Length: 65536\r\n" +
                ********************************************************/

                /*********************************************************
                * 页面主体前的一个空行不能省
                ********************************************************/
                "\r\n";   // 但最后和消息文本之间空一行不能省
                output.write(httpHeader.getBytes());

                /*********************************************************
                * 页面文本主体
                ********************************************************/
                int count = fis.read(bytes, 0, BUFFER_SIZE);
                while (count != -1) {
                    output.write(bytes);
                    count = fis.read(bytes, 0, count);
                }
            } else {
                /* 错误消息 */
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: 23\r\n" +
                    "\r\n" +
                    "<h1>File Not Found</h1>";
                output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            // thrown if cannot instantiate a File object
            System.out.println(e.toString() );
        } finally {
            if (fis!=null) { fis.close(); }
        }
    }
    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            File file = new File(HttpServer.WEB_ROOT, request.getUri());
            if (file.exists()) {
                /*********************************************************
                 * 打开资源文件
                 ********************************************************/
                System.out.println("Client is asking for " + file.getName());
                fis = new FileInputStream(file);

                /*********************************************************
                 * 先缓存所有正文，并计算长度长度。
                 ********************************************************/
                int totalLen = 0; // 资源长度
                byte[] info = new byte[TOTAL_SIZE]; // 缓存
                int count = fis.read(bytes, 0, BUFFER_SIZE);
                while (true) {
                    if (count == -1) { break; }
                    for (int i = 0; i < count; i++) {
                        if (totalLen == info.length) { info = Arrays.copyOf(info,info.length * 2); }
                        info[totalLen++] = bytes[i];
                    }
                    count = fis.read(bytes, 0, BUFFER_SIZE);
                }
                /*********************************************************
                 * 编辑带有"状态行"和"响应头"的HTTP头
                 ********************************************************/
                String httpHeader = "HTTP/1.1 200 OK\r\n" +
                    "Server: SHEN's First Java HttpServer\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + String.valueOf(totalLen) + "\r\n" +
                    "\r\n";

                /*********************************************************
                 * 写HTTP头
                 ********************************************************/
                output.write(httpHeader.getBytes());
                output.write(info, 0, totalLen);
            } else {
                /* 错误消息 */
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 23\r\n" +
                "\r\n" +
                "<h1>File Not Found</h1>";
                output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            // thrown if cannot instantiate a File object
            System.out.println(e.toString() );
        } finally {
            if (fis!=null) { fis.close(); }
        }
    }
}

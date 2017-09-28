package com.ciaoshen.howtomcatworks.ex02;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;

class Response implements ServletResponse { // 仅包内可见，不要影响其他章节的练习

  private static final int BUFFER_SIZE = 1024;
  Request request;
  OutputStream output;
  PrintWriter writer;

  public Response(OutputStream output) {
    this.output = output;
  }

  public void setRequest(Request request) {
    this.request = request;
  }

  /* This method is used to serve a static page */
  public void sendStaticResource() throws IOException {
    byte[] bytes = new byte[BUFFER_SIZE];
    FileInputStream fis = null;
    try {
      /* request.getUri has been replaced by request.getRequestURI */
      File file = new File(Constants.WEB_ROOT, request.getUri());
      System.out.println("Web Root = " + Constants.WEB_ROOT);
      System.out.println("File Path = " + file.getName());
      fis = new FileInputStream(file);
      /*
         HTTP Response = Status-Line
           *(( general-header | response-header | entity-header ) CRLF)
           CRLF
           [ message-body ]
         Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
      */
      String httpHeader = "HTTP/1.1 200 OK\r\n\r\n"; // 最简单的HTTP消息头，只有状态行
      output.write(httpHeader.getBytes());
      System.out.println(httpHeader);
      int ch = fis.read(bytes, 0, BUFFER_SIZE);
      while (ch!=-1) {
        output.write(bytes, 0, ch);
        System.out.println(new String(bytes));
        ch = fis.read(bytes, 0, BUFFER_SIZE);
      }
    }
    catch (FileNotFoundException e) {
      String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
        "Content-Type: text/html\r\n" +
        "Content-Length: 23\r\n" +
        "\r\n" +
        "<h1>File Not Found</h1>";
      output.write(errorMessage.getBytes());
    }
    finally {
      if (fis!=null)
        fis.close();
    }
  }


  /** implementation of ServletResponse  */
  public void flushBuffer() throws IOException {
  }

  public int getBufferSize() {
    return 0;
  }

  public String getCharacterEncoding() {
    return null;
  }

  public Locale getLocale() {
    return null;
  }

  public ServletOutputStream getOutputStream() throws IOException {
      return null;
  }

  /**
   * 为了向浏览器传输图片，不需要字符流，要的是字节流。
   * 看起来拿到的是一个面向字符流PrintWriter类的实例，实际上它是面向字节流的。
   * 这里output是一个OutputStream类的实例，
   * 利用PrintWriter(OutputStream out)构造器，实际是利用一个中介：OutputStreamWriter
   * 先把OutputStream包装成OutputStreamWriter，再包装成BufferedWriter，
   * 最后再构造成PrintWriter。
   *
   * 这样做，PrintWriter的write()方法，最终调用的是OutputStream的print()方法，
   * 最终达到用PrintWriter来输出字节流的目的。
   *
   * PrintWriter(OutputStream out)构造器会再调用另一个有两个参数的构造器，
   *
   *   public PrintWriter(OutputStream out, boolean autoFlush) {
   *        this(new BufferedWriter(new OutputStreamWriter(out)), autoFlush);
   *
   *        // save print stream for error propagation
   *        if (out instanceof java.io.PrintStream) {
   *            psOut = (PrintStream) out;
   *   }
   *
   */
  public PrintWriter getWriter() throws IOException {
    // autoflush is true, println() will flush,
    // but print() will not.

    // output是OutputStream类实例
    writer = new PrintWriter(output, true);
    return writer;
    // return null;
  }

  public boolean isCommitted() {
    return false;
  }

  public void reset() {
  }

  public void resetBuffer() {
  }

  public void setBufferSize(int size) {
  }

  public void setContentLength(int length) {
  }

  public void setContentType(String type) {
  }

  public void setLocale(Locale locale) {
  }
}

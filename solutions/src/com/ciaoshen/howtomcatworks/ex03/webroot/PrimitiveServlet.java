package com.ciaoshen.howtomcatworks.ex03.webroot;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class PrimitiveServlet implements Servlet {

  public void init(ServletConfig config) throws ServletException {
    System.out.println("Primitive Servlet init");
  }

  public void service(ServletRequest request, ServletResponse response)
    throws ServletException, IOException {
    System.out.println("Call PrimitiveServlet Service!");
    PrintWriter out = response.getWriter();
    /** HttpResponse里什么也没有，输出信息全靠硬编码打印 */
    out.println("HTTP/1.1 200 OK");
    out.println("");
    out.println("Hello. Roses are red.");
    out.print("Violets are blue.");
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

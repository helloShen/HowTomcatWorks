package com.ciaoshen.howtomcatworks.ex02;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.io.File;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/******************************************************************************
 * 一个Servlet服务器类，需要实现`Servlet`接口，
 *`ServletProcessor1`的任务就是，
 *  1. 解析收到的客户端请求
 *  2. 识别它请求的是静态资源，还是Servlet服务？
 *  3. 加载静态资源服务程序，或者Servlet服务程序。
 *
 * 解析URL要用到URL类，以及通过URLClassLoader类用一个URL定位并加载类文件。
 * 最后用Class#newInstance()方法，创建Servlet实例。
 *****************************************************************************/
public class ServletProcessor1 {

  public void process(Request request, Response response) {
    /**************************************************************************
     * 记住：servelet使用的URI格式如下：
     *      /servlet/servletName
     * servletName是请求的servlet资源的类名
     *
     * 一般URL的通用格式如下：
     *      scheme:[//[user[:password]@]host[:port]][/path][?query][#fragment]
     *
     * 所以，我们我们用的URL中的
     * user域，password域，host域，port域，query域，fragment域都为空。
     *
     * 第一个域scheme域指的是protocol。代码里选用的是file协议。
     *************************************************************************/
    String uri = request.getUri();
    /**
     * 我搜索“百里玄策”，收到的http请求如下，
     *      GET /index.html?king-of-glory-hero=%E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96
     *
     * 传进来的信息Request.getUri()是,
     *      index.html?king-of-glory-hero=%E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96
     *
     * 要确定用哪个servlet，需要把 "?xxxxxxxx=" 这段抠出来，比如，
     *      king-of-glory-hero 对应的是 KingOfGloryServlet
     */
    // String servletName = uri.substring(uri.lastIndexOf("/") + 1);
    String servletName = "";
    String servletId = uri.substring(uri.indexOf("?")+1,uri.indexOf("="));
    System.out.println("Servlet Id = " + servletId);
    switch (servletId) {
        case "king-of-glory-hero": servletName = "com.ciaoshen.howtomcatworks.ex02.KingOfGloryServlet"; break;
        default: break;
    }
    URLClassLoader loader = null;

    try {
      // create a URLClassLoader
      URL[] urls = new URL[1];
      URLStreamHandler streamHandler = null;
      File classPath = new File(Constants.CLASS_PATH);
      System.out.println(">>>>>> Class Path = \"" + classPath.getPath() + "\"");
      // the forming of repository is taken from the createClassLoader method in
      // org.apache.catalina.startup.ClassLoaderFactory
      /***********************************************************************
       * 注意！这里调用的是URL的下面这个构造器：
       *        public URL(String protocol, String host, String file)
       * 其中第一个参数"file"是指：file协议。
       * 第二个参数host被置空：null
       * 主要工作的是三个参数：file。它被赋值成classPath，即之前定义的WEB_ROOT，
       * 它指明了我们要到哪里去找Class。
       *
       * 上面这个三个参数的URL构造器最终调用的是一个有5个参数的构造器，
       *        public URL(String protocol, String host, int port, String file,
               URLStreamHandler handler)
       * 其中，port域被默认设为-1，handler域为null。
       **********************************************************************/
      String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
      System.out.println("Repository = " + repository);
      // the code for forming the URL is taken from the addRepository method in
      // org.apache.catalina.loader.StandardClassLoader class.
      /***********************************************************************
       * 注意！这里调用的是URL的下面这个构造器：
       *        public URL(URL context, String spec, URLStreamHandler handler)
       * 其中，第一个参数context和第三个参数handler都可以被设为null。
       * 第二个参数spec，就是之前构造的repository。就是告诉URLClassLoader到哪里去找Class.
       **********************************************************************/
      urls[0] = new URL(null, repository, streamHandler);
      loader = new URLClassLoader(urls);
    }
    catch (IOException e) {
      System.out.println(e.toString() );
    }
    Class<?> myClass = null;
    try {
      /******************************************************************
       * 所以，Servlet的本质就是用ClassLoader加载Class。
       *****************************************************************/
      System.out.println("Servlet Name = " + servletName);
      myClass = loader.loadClass(servletName);
    }
    catch (ClassNotFoundException e) {
      System.out.println(e.toString());
    }

    Servlet servlet = null;

    try {
      /******************************************************************
       * 创建已载入的servlet类的一个实例，并将其向下转型为javax.servlet.servlet
       * 并调用其service()方法。
       *****************************************************************/
      servlet = (Servlet) myClass.newInstance();
      servlet.service((ServletRequest) request, (ServletResponse) response);
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
    catch (Throwable e) {
      System.out.println(e.toString());
    }

  }
}

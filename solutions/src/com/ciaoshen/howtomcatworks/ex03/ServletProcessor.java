package com.ciaoshen.howtomcatworks.ex03;

import com.ciaoshen.howtomcatworks.ex03.connector.http.Constants;
import com.ciaoshen.howtomcatworks.ex03.connector.http.HttpRequest;
import com.ciaoshen.howtomcatworks.ex03.connector.http.HttpResponse;
import com.ciaoshen.howtomcatworks.ex03.connector.http.HttpRequestFacade;
import com.ciaoshen.howtomcatworks.ex03.connector.http.HttpResponseFacade;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import javax.servlet.Servlet;

public class ServletProcessor {

  public void process(HttpRequest request, HttpResponse response) {

    String uri = request.getRequestURI();
    String servletName = uri.substring(uri.lastIndexOf("/") + 1);
    URLClassLoader loader = null;
    try {
      // create a URLClassLoader
      URL[] urls = new URL[1];
      URLStreamHandler streamHandler = null;

      /** classPath = "/Users/Wei/github/HowTomcatWorks/solutions/bin" */
      File classPath = new File(Constants.CLASS_PATH);
      //   System.out.println("Class Path = " + classPath.getPath());

      /** repository = "file:/Users/Wei/github/HowTomcatWorks/solutions/bin/" */
      String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString() ;
      //   System.out.println("Repository = " + repository);

      /** URL = "/Users/Wei/github/HowTomcatWorks/solutions/bin/" */
      urls[0] = new URL(null, repository, streamHandler);
      //   System.out.println("URL = " + urls[0].getPath());

      loader = new URLClassLoader(urls);
    }
    catch (IOException e) {
      System.out.println(e.toString() );
    }
    Class myClass = null;
    try {
      /** Servlet Name = "com.ciaoshen.howtomcatworks.ex03.webroot.PrimitiveServlet" */
      myClass = loader.loadClass(Constants.PACK_WEB + "." + servletName);
      //   System.out.println("Servlet Name = " + servletName);
    }
    catch (ClassNotFoundException e) {
      System.out.println(e.toString());
    }

    Servlet servlet = null;

    try {
      servlet = (Servlet) myClass.newInstance();
      HttpRequestFacade requestFacade = new HttpRequestFacade(request);
      HttpResponseFacade responseFacade = new HttpResponseFacade(response);
      servlet.service(requestFacade, responseFacade);
      ((HttpResponse) response).finishResponse();
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
    catch (Throwable e) {
      System.out.println(e.toString());
    }
  }
}

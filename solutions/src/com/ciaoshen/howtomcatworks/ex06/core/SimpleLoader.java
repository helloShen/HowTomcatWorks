package com.ciaoshen.howtomcatworks.ex06.core;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.DefaultContext;

public class SimpleLoader implements Loader, Lifecycle {

    /**
     * 不要使用下面这个路径，因为user.dir拿到的是.sh文件运行程序时的路径。
     * 但如果用这个路径，程序照样能够运行，因为URLClassLoader会拿到运行时的classpath信息，
     * 所以还是能找到目标ModernServlet.class文件。
     *
     * 但还是不要用下面这个路径。
     */
    /**
    public static final String WEB_ROOT =
      System.getProperty("user.dir") + File.separator  + "webroot";
    */

    /**
     * 下面这个路径是正确的
     */
    public static final String ROOT =
       System.getProperty("user.home") +
       File.separator + "github" +
       File.separator + "HowTomcatWorks" +
       File.separator + "solutions";
    public static final String CP =
       ROOT +
       File.separator + "bin" +
       File.separator + "com" +
       File.separator + "ciaoshen" +
       File.separator + "howtomcatworks";

    public static final String WEB_ROOT =
       CP +
       File.separator + "ex06" +
       File.separator + "webroot";
  ClassLoader classLoader = null;
  Container container = null;

  public SimpleLoader() {
    try {
      URL[] urls = new URL[1];
      URLStreamHandler streamHandler = null;
      File classPath = new File(WEB_ROOT);
      String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString() ;
      System.out.println("URL = " + repository);
      urls[0] = new URL(null, repository, streamHandler);
      classLoader = new URLClassLoader(urls);
    }
    catch (IOException e) {
      System.out.println(e.toString() );
    }

  }
  public ClassLoader getClassLoader() {
    return classLoader;
  }

  public Container getContainer() {
    return container;
  }

  public void setContainer(Container container) {
    this.container = container;
  }

  public DefaultContext getDefaultContext() {
    return null;
  }

  public void setDefaultContext(DefaultContext defaultContext) {
  }

  public boolean getDelegate() {
    return false;
  }

  public void setDelegate(boolean delegate) {
  }

  public String getInfo() {
    return "A simple loader";
  }

  public boolean getReloadable() {
    return false;
  }

  public void setReloadable(boolean reloadable) {
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
  }

  public void addRepository(String repository) {
  }

  public String[] findRepositories() {
    return null;
  }

  public boolean modified() {
    return false;
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
  }

  // implementation of the Lifecycle interface's methods
  public void addLifecycleListener(LifecycleListener listener) {
  }

  public LifecycleListener[] findLifecycleListeners() {
    return null;
  }

  public void removeLifecycleListener(LifecycleListener listener) {
  }

  public synchronized void start() throws LifecycleException {
    System.out.println("Starting SimpleLoader");
  }

  public void stop() throws LifecycleException {
  }

}

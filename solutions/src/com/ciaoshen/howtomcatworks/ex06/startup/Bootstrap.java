package com.ciaoshen.howtomcatworks.ex06.startup;

import com.ciaoshen.howtomcatworks.ex06.core.SimpleContext;
import com.ciaoshen.howtomcatworks.ex06.core.SimpleContextLifecycleListener;
import com.ciaoshen.howtomcatworks.ex06.core.SimpleContextMapper;
import com.ciaoshen.howtomcatworks.ex06.core.SimpleLoader;
import com.ciaoshen.howtomcatworks.ex06.core.SimpleWrapper;
import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Mapper;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

public final class Bootstrap {
  public static void main(String[] args) {
    Connector connector = new HttpConnector();
    Wrapper wrapper1 = new SimpleWrapper();
    wrapper1.setName("Primitive");
    // 类名必须带上完整的包路径
    wrapper1.setServletClass("com.ciaoshen.howtomcatworks.ex06.webroot.PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    // 类名必须带上完整的包路径
    wrapper2.setServletClass("com.ciaoshen.howtomcatworks.ex06.webroot.ModernServlet");

    Context context = new SimpleContext();
    context.addChild(wrapper1);
    context.addChild(wrapper2);

    Mapper mapper = new SimpleContextMapper();
    mapper.setProtocol("http");
    LifecycleListener listener = new SimpleContextLifecycleListener();
    ((Lifecycle) context).addLifecycleListener(listener);
    context.addMapper(mapper);
    Loader loader = new SimpleLoader();
    context.setLoader(loader);
    // context.addServletMapping(pattern, name);
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");
    connector.setContainer(context);
    try {
      connector.initialize();
      ((Lifecycle) connector).start();
      ((Lifecycle) context).start();

      // make the application wait until we press a key.
      System.in.read();
      ((Lifecycle) context).stop();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

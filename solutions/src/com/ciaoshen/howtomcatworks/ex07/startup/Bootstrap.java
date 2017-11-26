package com.ciaoshen.howtomcatworks.ex07.startup;

import com.ciaoshen.howtomcatworks.ex07.core.SimpleContext;
import com.ciaoshen.howtomcatworks.ex07.core.SimpleContextLifecycleListener;
import com.ciaoshen.howtomcatworks.ex07.core.SimpleContextMapper;
import com.ciaoshen.howtomcatworks.ex07.core.SimpleLoader;
import com.ciaoshen.howtomcatworks.ex07.core.SimpleWrapper;
import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.logger.FileLogger;
import org.apache.catalina.Mapper;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

public final class Bootstrap {
  public static void main(String[] args) {
    Connector connector = new HttpConnector();
    Wrapper wrapper1 = new SimpleWrapper();
    wrapper1.setName("Primitive");
    // 必须用类的全具名
    wrapper1.setServletClass("com.ciaoshen.howtomcatworks.ex07.webroot.PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    // 必须用类的全具名
    wrapper2.setServletClass("com.ciaoshen.howtomcatworks.ex07.webroot.ModernServlet");
    Loader loader = new SimpleLoader();

    Context context = new SimpleContext();
    context.addChild(wrapper1);
    context.addChild(wrapper2);

    Mapper mapper = new SimpleContextMapper();
    mapper.setProtocol("http");
    LifecycleListener listener = new SimpleContextLifecycleListener();
    ((Lifecycle) context).addLifecycleListener(listener);
    context.addMapper(mapper);
    context.setLoader(loader);
    // context.addServletMapping(pattern, name);
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");

    // ------ add logger --------
    System.setProperty("catalina.base", System.getProperty("user.dir"));
    FileLogger logger = new FileLogger();
    logger.setPrefix("FileLog_");
    logger.setSuffix(".txt");
    logger.setTimestamp(true);
    logger.setDirectory("webroot");
    context.setLogger(logger);

    //---------------------------

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

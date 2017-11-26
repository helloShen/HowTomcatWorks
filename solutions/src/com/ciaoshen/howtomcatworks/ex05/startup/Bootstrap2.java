package com.ciaoshen.howtomcatworks.ex05.startup;

import com.ciaoshen.howtomcatworks.ex05.core.SimpleContext;
import com.ciaoshen.howtomcatworks.ex05.core.SimpleContextMapper;
import com.ciaoshen.howtomcatworks.ex05.core.SimpleLoader;
import com.ciaoshen.howtomcatworks.ex05.core.SimpleWrapper;
import com.ciaoshen.howtomcatworks.ex05.valves.ClientIPLoggerValve;
import com.ciaoshen.howtomcatworks.ex05.valves.HeaderLoggerValve;
import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import org.apache.catalina.Mapper;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

public final class Bootstrap2 {
  public static void main(String[] args) {
    HttpConnector connector = new HttpConnector();
    Wrapper wrapper1 = new SimpleWrapper();
    wrapper1.setName("Primitive");
    // 必须是类的全具名
    wrapper1.setServletClass("com.ciaoshen.howtomcatworks.ex06.webroot.PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    // 必须是类的全具名
    wrapper2.setServletClass("com.ciaoshen.howtomcatworks.ex06.webroot.ModernServlet");

    Context context = new SimpleContext();
    context.addChild(wrapper1);
    context.addChild(wrapper2);

    Valve valve1 = new HeaderLoggerValve();
    Valve valve2 = new ClientIPLoggerValve();

    ((Pipeline) context).addValve(valve1);
    ((Pipeline) context).addValve(valve2);

    Mapper mapper = new SimpleContextMapper();
    mapper.setProtocol("http");
    context.addMapper(mapper);
    Loader loader = new SimpleLoader();
    context.setLoader(loader);
    // context.addServletMapping(pattern, name);
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");
    connector.setContainer(context);
    try {
      connector.initialize();
      connector.start();

      // make the application wait until we press a key.
      System.in.read();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

package com.ciaoshen.howtomcatworks.ex10.startup;

import com.ciaoshen.howtomcatworks.ex10.core.SimpleWrapper;
import com.ciaoshen.howtomcatworks.ex10.core.SimpleContextConfig;
import com.ciaoshen.howtomcatworks.ex10.realm.SimpleUserDatabaseRealm;

import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Realm;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityCollection;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.loader.WebappLoader;

public final class Bootstrap2 {
  public static void main(String[] args) {

  //invoke: http://localhost:8080/Modern or  http://localhost:8080/Primitive

    System.setProperty("catalina.base", "/Users/Wei/github/HowTomcatWorks/webapps/");
    Connector connector = new HttpConnector();
    Wrapper wrapper1 = new SimpleWrapper();
    wrapper1.setName("Primitive");
    wrapper1.setServletClass("PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    wrapper2.setName("Modern");
    wrapper2.setServletClass("ModernServlet");

    Context context = new StandardContext();
    // StandardContext's start method adds a default mapper
    context.setPath("/app10");
    context.setDocBase("app10");
    LifecycleListener listener = new SimpleContextConfig();
    ((Lifecycle) context).addLifecycleListener(listener);

    context.addChild(wrapper1);
    context.addChild(wrapper2);
    // for simplicity, we don't add a valve, but you can add
    // valves to context or wrapper just as you did in Chapter 6

    Loader loader = new WebappLoader();
    context.setLoader(loader);
    // context.addServletMapping(pattern, name);
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");
    // add ContextConfig. This listener is important because it configures
    // StandardContext (sets configured to true), otherwise StandardContext
    // won't start

    // add constraint
    SecurityCollection securityCollection = new SecurityCollection();
    securityCollection.addPattern("/");
    securityCollection.addMethod("GET");

    SecurityConstraint constraint = new SecurityConstraint();
    constraint.addCollection(securityCollection);
    constraint.addAuthRole("manager");
    LoginConfig loginConfig = new LoginConfig();
    loginConfig.setRealmName("Simple User Database Realm");
    /** add realm. 和Bootstrap1不同，使用SimpleUserDatabaseRealm作为Realm的实现类 */
    System.out.println("Create Realm: SimpleUserDatabaseRealm ... ...");
    Realm realm = new SimpleUserDatabaseRealm();
    System.out.println("SimpleUserDatabaseRealm created!");
    System.out.println("Loading users datas from tomcat-users.xml ... ...");
    ((SimpleUserDatabaseRealm) realm).createDatabase("conf/tomcat-users.xml");
    System.out.println("User datas loaded!");
    context.setRealm(realm);
    context.addConstraint(constraint);
    context.setLoginConfig(loginConfig);

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

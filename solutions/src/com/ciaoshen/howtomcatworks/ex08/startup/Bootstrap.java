package com.ciaoshen.howtomcatworks.ex08.startup;

import com.ciaoshen.howtomcatworks.ex08.core.MyWebappLoader;
import com.ciaoshen.howtomcatworks.ex08.core.MyWebappClassLoader;
import com.ciaoshen.howtomcatworks.ex08.core.SimpleWrapper;
import com.ciaoshen.howtomcatworks.ex08.core.SimpleContextConfig;
import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.core.StandardContext;
// 用MyWebappLoader和MyWebappClassLoader代替
// import org.apache.catalina.loader.WebappClassLoader;
// import org.apache.catalina.loader.WebappLoader;
import org.apache.naming.resources.ProxyDirContext;
import java.net.URL;
import java.net.URLClassLoader;

public final class Bootstrap {
  public static void main(String[] args) {

    //invoke: http://localhost:8080/Modern or  http://localhost:8080/Primitive

    /*
     *  原先用"user.dir"设置CATALINA_HOME不可行，
     *  因为"user.dir"是我程序运行的地方，不在/Users/Wei/github/HowTomcatWorks/
     *  而是在/Users/Wei/github/HowTomcatWorks/solutions/sh/ex08/
     */
    // System.setProperty("catalina.base", System.getProperty("user.dir"));

    /**
     * 先创建连接器
     */
    System.setProperty("catalina.base", System.getProperty("user.home") + "/github/HowTomcatWorks/");
    Connector connector = new HttpConnector();
    /**
     * 再创建内部小容器Wrapper
     */
    Wrapper wrapper1 = new SimpleWrapper();
    /** 设置Wrapper的name字段，作为每个Wrapper的标识符，后面映射器要利用这个字段查找用户请求的Wrapper容器 */
    wrapper1.setName("Primitive");
    /** Servlet应用程序的全具名（已经包含包名） */
    wrapper1.setServletClass("PrimitiveServlet");
    Wrapper wrapper2 = new SimpleWrapper();
    /** 同上 */
    wrapper2.setName("Modern");
    /** 同上 */
    wrapper2.setServletClass("ModernServlet");

    /**
     * 创建主Context容器
     */
    Context context = new StandardContext();
    /**
     * StandardContext's start() 会创建一个默认的Mapper
     * 通过addDefaultMapper()方法。
     * 默认Mapper信息硬编码在StandardContext#mapperClass字段：
     *     private String mapperClass = "org.apache.catalina.core.StandardContextMapper";
     */
    /**
     * setPath()方法设置Context的name字段。
     * 每个容器都有一个name字段，用来标识这个容器。不代表根目录。
     * Engine,Host,Context,Wrapper都有name字段。
     * 后面会被用来构建work目录。
     */
    context.setPath("webapps/app1");
    System.out.println("StandardContext#name = " + context.getName());
    /**
     * 最重要的一个设置：
     * Doc Base 相当于这个Context的根目录。官方解释如下：
     *      Set the document root for this Context.
     *      This can be an absolute pathname, a relative pathname, or a URL.
     * 后面的Repositories就是根据根据下面规则创建出来：
     *      CATALINA_HOME/DOC_BASE/WEB-INF/classes
     *      CATALINA_HOME/DOC_BASE/WEB-INF/lib
     */
    context.setDocBase("/webapps/app1");
    /**
     * 把2个小Wrapper容器绑定到Context容器中。
     */
    context.addChild(wrapper1);
    context.addChild(wrapper2);
    /**
     * Request请求消息片段，到Wrapper "name" 的映射。
     * 这个name就是刚才Wrapper#setName()函数设置的信息。
     */
    context.addServletMapping("/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");
    /**
     * add ContextConfig. This listener is important because it configures
     * StandardContext (sets configured to true), otherwise StandardContext
     * won't start
     */
    LifecycleListener listener = new SimpleContextConfig();
    ((Lifecycle) context).addLifecycleListener(listener);

    /**
     * 实际使用的是org.apache.catalina.loader.WebappLoader类。
     * com.ciaoshen.howtomcatworks.ex08.core.MyWebappLoader只是一个简单拷贝，为了在中间插入log()打印消息，方便我调试。
     * WebappLoader的无参数默认构造器：
     * Construct a new WebappLoader with no defined parent class loader
     * (so that the actual parent will be the system class loader).
     */
    Loader loader = new MyWebappLoader();
    /** associate the loader with the Context */
    context.setLoader(loader);
    /** 此时classLoader字段还为null */
    System.out.println("Associated Class Loader is: " + loader.getClassLoader());

    /**
     * 把连接器和主容器相关联。
     */
    connector.setContainer(context);

    try {
      /** 初始化连接器 */
      connector.initialize();
      ((Lifecycle) connector).start();


      /** Resources is null before start() method */
      System.out.println("Before start() method, Context Resources is null? " + (context.getResources() == null));
      /**
       * 1. 构造WebappClassLoader（等同于MyWebappClassLoader。MyWebappClassLoader只是一个拷贝)
       * Context#start()会调用WebappLoader对象的start()函数，
       * 转而调用WebappLoader#createClassLoader()方法创建默认的类载入器
       * createClassLoader()方法会根据WebappLoader#loaderClass字段给出的全具名创建实例
       * loaderClass字段是硬编码字符串，
       * 默认为：org.apache.catalina.loader.WebappClassLoader
       * 可以通过setLoaderClass()方法修改。
       *
       * 2. 创建Work工作目录
       * 这行代码对应的通知台输出（MyWebappLoader等同于WebappLoader）：
       *     MyWebappLoader[webapp_app1]: Deploying class repositories to work directory /Users/Wei/github/HowTomcatWorks/work/_/_/webapp_app1/
       *     Starting Wrapper Primitive
       *     Starting Wrapper Modern
       *     StandardManager[/bin]: Seeding random number generator class java.security.SecureRandom
       *     StandardManager[/bin]: Seeding of random number generator has been completed
       *
       * 并且实际创建一个文件夹：/Users/Wei/github/HowTomcatWorks//work/_/_/webapp_app1/
       * 因为Context#start()函数会调用Context#postWorkDirectory()方法，
       * 这个方法会创建一个工作目录，路径格式如下：
       *    CATALINA_BASE/work/ENGINE_NAME/HOST_NAME/CONTEXT_NAME/
       * 当ENGINE_NAME和HOST_NAME为空的时候，会用"_"补足。并且CONTEXT_NAME的斜杠"/"也会被下划线"_"代替，最终得到上面这个路径。
       *
       * 3. 设置Resources
       * Context#start()函数还会调用ContainerBase#setResources()函数为Context#resources字段赋值。
       * Context#resources字段是DirContext型。
       * 赋值的依据是之前用Context.setDocBase()函数设置的docBase字段。
       * 然后在WebappLoader#start()里调用WebappClassLoader#setResources()函数把Context#resources字段赋值给WebappClassLoader#resources
       * 构造的规则如下：
       *        CATALINA_BASE/DOC_BASE/
       * 最终，Context#docBase字段为：
       *        /webapp/app1
       * WebappClassLoader里的docBase字段为：
       *        /Users/Wei/github/HowTomcatWorks/webapp/app1
       *
       * 4. 设置Repositories
       * Repositories是WebappClassLoader实际查找Servlet类文件的地方。默认有两个位置：
       *        CATALINA_BASE/DOC_BASE/WEB-INF/classes: 放class类文件
       *        CATALINA_BASE/DOC_BASE/WEB-INF/lib: 放jar库
       * 具体调用过程如下：
       *        StandardContext#start()调用WebappLoader#start()函数
       *        WebappLoader#start()调用WebappLoader#setRepositories()函数
       * "/WEB-INF/classes"和"WEB-INF/lib"两个子路径硬编码在WebappLoader#setRepositories()函数里
       * 最终我的repositories绝对路径如下，
       *        /Users/Wei/github/HowTomcatWorks/webapp/app1/WEB-INF/classes
       *        /Users/Wei/github/HowTomcatWorks/webapp/app1/WEB-INF/lib
       */
      ((Lifecycle) context).start();



      /************************************************************
       * now we want to know some details about WebappLoader
       ***********************************************************/

      /**
       * 控制台打印出：
       * After Context#start(), Class Loader is: MyWebappClassLoader
       * available:
       * delegate: false
       * repositories:
       *   /WEB-INF/classes/
       * required:
       ----------> Parent Classloader:
       sun.misc.Launcher$AppClassLoader@659e0bfd
       *
       * 注：
       * 最后的AppClassLoader@659e0bfd表示“System Class Loader”系统类加载器
       */
      System.out.println("After Context#start(), Class Loader is: " + context.getLoader().getClassLoader());
      MyWebappClassLoader classLoader = (MyWebappClassLoader) loader.getClassLoader();

      /**
       * 控制台输出如下：(MyWebappClassLoader等同于WebappClassLoader)
       * Resources is instance of org.apache.naming.resources.ProxyDirContext
       * StandardContext Resources' docBase: /webapps/app1
       * MyWebappClassLoader Resources' docBase: /Users/Wei/github/HowTomcatWorks/webapps/app1
       * MyWebappClassLoader Repositories:
       *     repository: /WEB-INF/classes/
       * MyWebappClassLoader URLs:
       *     URL: file:/Users/Wei/github/HowTomcatWorks/webapps/app1/WEB-INF/classes/
       */
      System.out.println("Resources is instance of " + classLoader.getResources().getClass().getName());
      System.out.println("StandardContext Resources' docBase: " + context.getDocBase());
      System.out.println("MyWebappClassLoader Resources' docBase: " + ((ProxyDirContext)classLoader.getResources()).getDocBase());
      String[] repositories = classLoader.findRepositories();
      System.out.println("MyWebappClassLoader Repositories: ");
      for (int i=0; i<repositories.length; i++) {
        System.out.println("  repository: " + repositories[i]);
      }
      URL[] urls = ((URLClassLoader)classLoader).getURLs();
      System.out.println("MyWebappClassLoader URLs: ");
      for (int i = 0; i < urls.length; i++) {
          System.out.println("  URL: " + urls[i]);
      }

      /**
       * 用户发出请求：
       *        http://127.0.0.1:8080/Modern
       * 控制台打印日志：
       *    ModernServlet -- init
       * Servlet ModernServlet, loaded by: MyWebappClassLoader
       *    available:
       *    delegate: false
       *    repositories:
       *        /WEB-INF/classes/
       *    required:
       * ----------> Parent Classloader:
       *    sun.misc.Launcher$AppClassLoader@659e0bfd
       *
       * 证明Servlet应用程序确实由应用加载器加载。
       */

      // make the application wait until we press a key.
      System.in.read();
      ((Lifecycle) context).stop();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

package com.ciaoshen.howtomcatworks.ex20.standardmbeantest;

import javax.management.Attribute;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

public class StandardAgent {

  private MBeanServer mBeanServer = null;

  public StandardAgent() {
    /**
     * 实际创建的是com.sun.jmx.mbeanserver.JmxMBeanServer类实例
     * 中间经过了：   javax.management.MBeanServerFactory
     *              javax.management.MBeanServerBuilder
     * JmxMBeanServer实现了MBeanServer接口
     */
    mBeanServer = MBeanServerFactory.createMBeanServer();
  }

  public MBeanServer getMBeanServer() {
    return mBeanServer;
  }

  public ObjectName createObjectName(String name) {
    ObjectName objectName = null;
    try {
      objectName = new ObjectName(name);
    }
    catch (Exception e) {
    }
    return objectName;
  }


  private void createStandardBean(ObjectName objectName, String managedResourceClassName) {
    try {
      /**
       * 1. 实际利用反射通过类的全具名创建CarMBean实例(实际是Car类实例,CarMBean只是个接口)，
       * 最终实际执行反射实例化操作的是MBeanInstantiator类，
       * 中间经过：
       *    com.sun.jmx.mbeanserver.JmxMBeanServer#createMBean()
       *    com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#createMBean()
       *    com.sun.jmx.mbeanserver.MBeanInstantiator#instantiate()
       * 2. 并将CarMBean实例(实际是Car类实例)注册到JmxMBeanServer实例中。CarMBean实例实际是封装在Repository类实例中：
       * 中间经过：
       *    com.sun.jmx.mbeanserver.JmxMBeanServer#createMBean()
       *    com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#registerObject()
       *    com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#registerDynamicMBean()
       *    com.sun.jmx.interceptor.DefaultMBeanServerInterceptor#internal_addObject()
       *    com.sun.jmx.mbeanserver.Repository#addMBean()
       * 3. 最终在Repository类中，储存MBean实例的数据结构就是一个HashMap：
       *    A Hashtable is used for storing the different domains For each domain,
       *    a hashtable contains the instances with canonical key property list
       *    string as key and named object aggregated from given object name and
       *    mbean instance as value.
       *        private final Map<String,Map<String,NamedObject>> domainTb;
       */
      mBeanServer.createMBean(managedResourceClassName, objectName);
    }
    catch(Exception e) {
    }
  }

  public static void main(String[] args) {
    /** 创建本测试程序的实例 */
    StandardAgent agent = new StandardAgent();
    /** 创建JMX默认的MBeanServer接口实现类JmxMBeanServer类实例 */
    MBeanServer mBeanServer = agent.getMBeanServer();
    /** 默认域为null */
    String domain = mBeanServer.getDefaultDomain();
    String managedResourceClassName = "com.ciaoshen.howtomcatworks.ex20.standardmbeantest.Car";
    /**
     * 实际的ObjectName为如下形式：
     *      null:type=com.ciaoshen.howtomcatworks.ex20.standardmbeantest.Car
     */
    ObjectName objectName = agent.createObjectName(domain + ":type=" +
      managedResourceClassName);
    /**
     * 通过上面的ObjectName创建MBean实例
     */
    agent.createStandardBean(objectName, managedResourceClassName);


    // manage MBean
    try {
      Attribute colorAttribute = new Attribute("Color","blue");
      mBeanServer.setAttribute(objectName, colorAttribute);
      System.out.println(mBeanServer.getAttribute(objectName, "Color"));
      mBeanServer.invoke(objectName,"drive",null,null);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }


}

package com.ciaoshen.howtomcatworks.ex15.digestertest;

import java.io.File;
import org.apache.commons.digester.Digester;

public class Test01 {

  public static void main(String[] args) {
    /** 原来的路径不行 */
    String path = System.getProperty("user.home") +
        File.separator + "github" +
        File.separator + "HowTomcatWorks" +
        File.separator  + "etc";

    /** 要用下面这个路径 */
    File file = new File(path, "employee1.xml");
    Digester digester = new Digester();
    // add rules
    digester.addObjectCreate("employee", "com.ciaoshen.howtomcatworks.ex15.digestertest.Employee");
    digester.addSetProperties("employee");
    digester.addCallMethod("employee", "printName");

    try {
      Employee employee = (Employee) digester.parse(file);
      System.out.println("First name : " + employee.getFirstName());
      System.out.println("Last name : " + employee.getLastName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

}

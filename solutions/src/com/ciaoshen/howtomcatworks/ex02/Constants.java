package com.ciaoshen.howtomcatworks.ex02;

import java.io.File;

public class Constants {
    /** WEB_ROOT is the directory where our HTML and other files reside.
     *  For this package, WEB_ROOT is the "webroot" directory under the working
     *  directory.
     *  The working directory is the location in the file system
     *  from where the java command was invoked.
     */
    public static final String ROOT =
      System.getProperty("user.home") +
      File.separator + "github" +
      File.separator + "HowTomcatWorks" +
      File.separator + "solutions";
    public static final String PACK =
      "com" +
      File.separator + "ciaoshen" +
      File.separator + "howtomcatworks" +
      File.separator + "ex02";
    public static final String SRC_PATH =
      ROOT +
      File.separator + "src" +
      File.separator + PACK;
    public static final String CLASS_PATH =
      ROOT +
      File.separator + "bin";
    public static final String WEB_ROOT =
      SRC_PATH + File.separator +
      File.separator + "webroot";
}

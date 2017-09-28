package com.ciaoshen.howtomcatworks.ex03.connector.http;
import java.io.File;

public final class Constants {
    public static final String ROOT =
      System.getProperty("user.home") +
      File.separator + "github" +
      File.separator + "HowTomcatWorks" +
      File.separator + "solutions";
    public static final String PACK_PATH =
      "com" +
      File.separator + "ciaoshen" +
      File.separator + "howtomcatworks" +
      File.separator + "ex03";
    public static final String PACK =
      "com.ciaoshen.howtomcatworks.ex03";
    public static final String PACK_CONN =
      PACK + ".connector";
    public static final String PACK_HTTP =
      PACK_CONN + ".http";
    public static final String PACK_WEB =
      PACK + ".webroot";
    public static final String SRC_PATH =
      ROOT +
      File.separator + "src";
    public static final String CLASS_PATH =
      ROOT +
      File.separator + "bin";
    public static final String WEB_ROOT =
      SRC_PATH +
      File.separator + PACK_PATH +
      File.separator + "webroot";
    public static final String WEB_CLASS =
      CLASS_PATH +
      File.separator + PACK_PATH +
      File.separator + "webroot";

  public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
  public static final int PROCESSOR_IDLE = 0;
  public static final int PROCESSOR_ACTIVE = 1;
}

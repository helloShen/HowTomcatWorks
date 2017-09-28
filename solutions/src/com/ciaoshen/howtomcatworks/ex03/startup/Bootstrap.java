package com.ciaoshen.howtomcatworks.ex03.startup;

import com.ciaoshen.howtomcatworks.ex03.connector.http.HttpConnector;

public final class Bootstrap {
  public static void main(String[] args) {
    /** 就是简单调用 HttpConnector */
    HttpConnector connector = new HttpConnector();
    connector.start();
  }
}

/*
 * 专门用来做一些小测试
 */
package com.ciaoshen.howtomcatworks.ex01;
import java.util.*;

class SomeTests {
    /** user.dir refers to where the java run from.
     *  It does not have to be within the user dir. */
    private static void printSysUserDir() {
        System.out.println("User Dir: " + System.getProperty("user.dir"));
    }
    /* user.home refers to the user dir. */
    private static void printSysUserHome() {
        System.out.println("User Home: " + System.getProperty("user.home"));
    }
    public static void main(String[] args) {
        printSysUserDir();
        printSysUserHome();
    }
}

/**
 * 我的机器上并没有重现打印0的情况
 */
package com.ciaoshen.howtomcatworks.ex04.concurrency;
import java.util.*;

class NonVisibility implements Runnable {
    private int number = 0;
    private boolean ready = false;
    public void run() {
        while (!ready) { ; }
        if (number == 0) {
            System.out.println("Number = " + number);
        }
    }
    public static void main(String[] args) {
        while (true) {
            NonVisibility non = new NonVisibility();
            new Thread(non).start();
            non.number = 42;
            non.ready = true;
        }
    }
}

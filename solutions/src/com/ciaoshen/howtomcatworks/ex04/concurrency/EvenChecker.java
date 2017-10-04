package com.ciaoshen.howtomcatworks.ex04.concurrency;
import java.util.*;

class EvenChecker implements Runnable {
    private static int count = 0;
    private int id;
    private EvenGenerator generator;
    private boolean stopped = false;
    public EvenChecker(EvenGenerator g) {
        id = ++count;
        generator = g;
    }
    // 可中断任务的良好实践
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                int val = generator.nextEven();
                if (val % 2 != 0) {
                    System.out.println("EvenChecker#" + id + " find val [" + val + "] is not even!");
                }
                Thread.sleep(1);
            }
        } catch (InterruptedException ie) {
                System.out.println("Thread#" + id + " stopped while sleep!");
                stopped = true;
        }
        System.out.println("Thread#" + id + " stopped correctly!");
    }
}

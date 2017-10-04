package com.ciaoshen.howtomcatworks.ex04.concurrency;
import java.util.concurrent.*;

class RaceCondition {
    public static void test(int size) {
        EvenGenerator g = new EvenGenerator();
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < size; i++) {
            exec.execute(new EvenChecker(g));
        }
        Thread.sleep(100);
        exec.shutdownNow();
    }
    public static void main(String[] args) {
        test(10);
    }
}

package com.ciaoshen.howtomcatworks.ex04.concurrency;
import java.util.*;
import java.util.concurrent.*;

class Resturant {
    private static volatile int count;
    private final int maxMeal;
    private volatile int meal;
    public Resturant(int max) { maxMeal = max; }

    public synchronized void offer() throws InterruptedException {
        while (meal == maxMeal) {
            System.out.println("Table if full! Chef#" + Thread.currentThread().getId() + " wait waiter to take some meals!");
            wait();
        }
        meal++;
        System.out.println("Chef#" + Thread.currentThread().getId() + " offer Meal#" + (++count) + " !");
        System.out.println(meal + " meals on table!");
        if (meal > 10) { System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"); }
        notifyAll();
        Thread.yield();
    }
    public synchronized void take() throws InterruptedException {
        while (meal == 0) {
            System.out.println("No more meal! Waiter#" + Thread.currentThread().getId() + " wait chef to offer some new meals!");
            wait();
        }
        meal--;
        System.out.println("Waiter#" + Thread.currentThread().getId() + " take a meal!");
        System.out.println(meal + " meal on table!");
        if (meal < 0) { System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"); }
        notifyAll();
        Thread.yield();
    }
    public static void main(String[] args) {
        for (int j = 0; j < 100; j++) {
            Resturant resturant = new Resturant(10);
            ExecutorService exec = Executors.newCachedThreadPool();
            // 10个厨师，10个服务员. 各自拥有独立线程.
            for (int i = 0; i < 10; i++) {
                exec.submit(new Chef(resturant));
                exec.submit(new Waiter(resturant));
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                System.out.println("Resturant thread interrupted accidently!");
            }
            exec.shutdownNow();
        }
    }
}

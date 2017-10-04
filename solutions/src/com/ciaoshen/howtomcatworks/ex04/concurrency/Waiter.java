package com.ciaoshen.howtomcatworks.ex04.concurrency;
import java.util.*;
import java.util.concurrent.*;

class Waiter implements Runnable {
    private Resturant resturant;
    public Waiter(Resturant r) {
        synchronized(this) { resturant = r; }
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                resturant.take();
            }
        } catch (InterruptedException ie) {
            System.out.println("Waiter#" + Thread.currentThread().getId() + " interrupted during waiting!");
        }
        System.out.println("Waiter#" + Thread.currentThread().getId() + " stopped correctly!");
    }
}

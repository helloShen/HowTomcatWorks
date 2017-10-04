package com.ciaoshen.howtomcatworks.ex04.concurrency;
import java.util.*;
import java.util.concurrent.*;

class Chef implements Runnable {
    private Resturant resturant;
    public Chef(Resturant r) {
        synchronized(this) { resturant = r; }
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                resturant.offer();
            }
        } catch (InterruptedException ie) {
            System.out.println("Chef#" + Thread.currentThread().getId() + " interrupted during waiting!");
        }
        System.out.println("Chef#" + Thread.currentThread().getId() + " stopped correctly!");
    }
}

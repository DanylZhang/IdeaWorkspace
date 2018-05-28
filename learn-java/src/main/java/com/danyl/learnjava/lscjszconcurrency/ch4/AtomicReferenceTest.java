package com.danyl.learnjava.lscjszconcurrency.ch4;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceTest {

    public final static AtomicReference<String> atomicStr = new AtomicReference<>("abc");

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            final int num = i;
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep((int) (Math.random() * 100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (atomicStr.compareAndSet("abc", "def")) {
                        System.out.println("Thread:" + Thread.currentThread().getId() + " change value to " + atomicStr);
                    } else {
                        System.out.println("Thread:" + Thread.currentThread().getId() + " FAILED");
                    }
                }
            }.start();
        }
    }
}

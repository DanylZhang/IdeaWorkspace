package com.danyl.learnjava.lscjszconcurrency.ch10;

import java.util.ArrayList;

public class UnsafeArrayList {
    static ArrayList arrayList = new ArrayList();

    static class AddTask implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 10000000; i++) {
                arrayList.add(new Object());
            }
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new AddTask(), "t1");
        Thread t2 = new Thread(new AddTask(), "t2");
        t1.start();
        t2.start();
        Thread t3 = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t3");
        t3.start();
    }
}

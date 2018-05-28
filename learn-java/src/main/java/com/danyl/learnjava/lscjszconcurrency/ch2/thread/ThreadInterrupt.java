package com.danyl.learnjava.lscjszconcurrency.ch2.thread;

public class ThreadInterrupt {

    public static void main(String[] args) {

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Interrupted!");
                        break;
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted when sleep!");
                        // try-catch捕获后会清除中断标记,需手动补偿interrupt
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        t1.start();
    }
}

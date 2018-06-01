package com.danyl.learnjava.lscjszconcurrency.ch5.deadlock;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class DeadLockChecker {

    private final static ThreadMXBean mbean = ManagementFactory.getThreadMXBean();

    final static Runnable deadlockCheck = new Runnable() {
        @Override
        public void run() {
            while (true) {
                long[] deadlockedThreadIds = mbean.findDeadlockedThreads();
                if (deadlockedThreadIds != null) {
                    ThreadInfo[] threadInfos = mbean.getThreadInfo(deadlockedThreadIds);
                    for (Thread thread : Thread.getAllStackTraces().keySet()) {
                        for (int i = 0; i < threadInfos.length; i++) {
                            if (thread.getId() == threadInfos[i].getThreadId()) {
                                thread.interrupt();
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public static void check() {
        Thread thread = new Thread(deadlockCheck);
        thread.setDaemon(true);
        thread.start();
    }
}

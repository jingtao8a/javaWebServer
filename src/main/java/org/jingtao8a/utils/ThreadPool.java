package org.jingtao8a.utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool<T extends Runnable> {
    private List<Thread> threadList;
    private BlockQueue<T> blockQueue;
    private AtomicBoolean running = new AtomicBoolean();
    public ThreadPool(int queueSize, int ThreadNum) {
        blockQueue = new BlockQueue<>(queueSize);
        threadList = new ArrayList<>();
        running.set(true);
        for (int i = 0; i < ThreadNum; ++i) {
            threadList.add(new MyThread(i));
        }
        for (int i = 0; i < ThreadNum; ++i) {
            threadList.get(i).start();
        }
    }

    public boolean addTask(T task) throws InterruptedException {
        if (blockQueue.isFull()) {
            return false;
        }
        blockQueue.put(task);
        return true;
    }
    public void stop() throws InterruptedException {
        running.set(false);
        for (Thread thread: threadList) {
            thread.interrupt();
            thread.join();
        }
        while (!blockQueue.isEmpty()) {
            blockQueue.get().run();
        }
    }
    class MyThread extends Thread {
        private int i;
        public MyThread(int i) {
            this.i = i;
        }
        @Override
        public void run() {
            System.out.println("thread" + i);
            while (running.get()) {
                try {
                    T task = blockQueue.get();
                    task.run();
                } catch (InterruptedException e) {
                    System.out.println("thread" + i + "interrupt");
                }
            }
        }
    }
}

package org.jingtao8a.utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool<T extends Runnable> {
    private List<Thread> threadList;
    private BlockQueue<T> blockQueue;
    private AtomicBoolean running = new AtomicBoolean();
    public ThreadPool(int queueSize, int ThreadNum) {
        blockQueue = new BlockQueue<>(queueSize);
        threadList = new ArrayList<>(ThreadNum);
        running.set(true);
        for (int i = 0; i < ThreadNum; ++i) {
            threadList.add(new MyThread(this.blockQueue));
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
            thread.join();
        }
        while (!blockQueue.isEmpty()) {
            blockQueue.get().run();
        }
    }
    class MyThread extends Thread {
        private BlockQueue<T> blockQueue;
        public MyThread(BlockQueue<T> blockQueue) {
            this.blockQueue = blockQueue;
        }
        @Override
        public void run() {
            while (running.get()) {
                try {
                    T task = this.blockQueue.get();
                    task.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

package org.jingtao8a.test;

import org.jingtao8a.utils.BlockQueue;
import org.junit.Test;

public class testBlockQueue {
    BlockQueue<String> queue = new BlockQueue<>(4);
    @Test
    public void testBlockQueue() throws InterruptedException {
        Thread[] threads= new Thread[8];
        for (int i = 0; i < 4; ++i) {
            threads[i] = new Consumer(i + 1);
        }
        for (int i = 4; i < 8; ++i) {
            threads[i] = new Producer(i + 1);
        }
        for (int i = 0; i < 8; ++i) {
            threads[i].start();
        }
        for (int i = 0; i < 8; ++i) {
            threads[i].join();
        }
    }

    class Consumer extends Thread {
        private int i;
        public Consumer(int i) {this.i = i;}

        @Override
        public void run() {
            try {
                for (int j = (i - 1) * 10; j < i * 10; ++j) {
                    queue.put(String.format("thread put %d", j));
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Producer extends Thread {
        private int i;
        public Producer(int i) {this.i = i;}
        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; ++i)
                    System.out.println(queue.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

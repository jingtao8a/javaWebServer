package org.jingtao8a.test;

import org.junit.Test;
import org.jingtao8a.utils.ThreadPool;
public class testThreadPool {
    @Test
    public void testThreadPool() throws InterruptedException {
        ThreadPool<Task> threadPool = new ThreadPool<>(2,4);
        for (int i = 0; i < 8; ++i) {
            Task task = new Task(i + 1);
            while (!threadPool.addTask(task));
        }
        threadPool.stop();
    }

    class Task implements Runnable {
        private int i;
        public Task(int i) {this.i = i;}

        @Override
        public void run() {
            for (int j = (i - 1) * 10; j < i * 10; ++j) {
                System.out.println((String.format("thread put %d", j)));
            }
        }
    }
}

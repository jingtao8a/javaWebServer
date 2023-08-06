package org.jingtao8a.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockQueue<T> {
    private Object[] array;
    private int head = 0, tail = 0;
    private int size;
    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();

    public BlockQueue(int capacity) {
        this.array = new Object[capacity];
    }

    public boolean isFull() {
        lock.lock();
        try {
            return size >= array.length;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return size == 0;
        } finally {
            lock.unlock();
        }
    }
    public void put(T o) throws InterruptedException {
        lock.lock();
        try {
            while (size >= array.length) {
                notFull.await();
            }
            ++size;
            tail = (tail + 1) % array.length;
            array[tail] = o;
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T get() throws InterruptedException {
        lock.lock();
        try {
            while (size == 0) {
                notEmpty.await();
            }
            head = (head + 1) % array.length;
            T res = (T)array[head];
            --size;
            notFull.signalAll();
            return res;
        } finally {
            lock.unlock();
        }
    }
}

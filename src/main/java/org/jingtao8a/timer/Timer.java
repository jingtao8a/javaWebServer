package org.jingtao8a.timer;

import java.util.Comparator;

public class Timer implements Comparable<Timer> {
    private Runnable task;
    private long time;//执行时间

    public Timer(Runnable task, long after) {
        this.task = task;
        this.time = System.currentTimeMillis() + after;
    }

    public long getTime() {
        return time;
    }
    @Override
    public int compareTo(Timer obj) {
        return (int)(this.time - obj.time);
    }

    public void run() {
        task.run();
    }
}

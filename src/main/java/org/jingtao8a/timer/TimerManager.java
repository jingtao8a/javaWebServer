package org.jingtao8a.timer;

import java.util.*;

public class TimerManager {
    private PriorityQueue<Timer> heap = new PriorityQueue<>();
    public long getNextTick() {
        if (heap.size() == 0) {
            return 0;
        }
        long res = heap.peek().getTime() - System.currentTimeMillis();
        return res < 0 ? 0 : res;
    }
    public List<Timer> getExpiredTimerList() {
        List<Timer> timerList = new ArrayList<>();
        long curTime= System.currentTimeMillis();
        Timer timer = heap.peek();
        while (timer != null && timer.getTime() <= curTime) {
            heap.remove(timer);
            timerList.add(timer);
            timer = heap.peek();
        }
        return timerList;
    }

    public void doExpiredTask() {
        List<Timer> timerList = getExpiredTimerList();
        for (Timer timer: timerList) {
            timer.run();
        }
    }

    public void addTimer(Timer timer) {
        heap.add(timer);
    }
}

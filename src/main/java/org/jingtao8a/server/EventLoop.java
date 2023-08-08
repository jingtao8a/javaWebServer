package org.jingtao8a.server;

import lombok.Getter;
import org.jingtao8a.timer.TimerManager;

import java.util.*;

@Getter
public class EventLoop {
    private TimerManager timerManager = new TimerManager();
    private Epoller epoller;
    private final long timeSlot = 1000;
    public EventLoop() {
        epoller = new Epoller(timeSlot,this);
    }

    public void loop() {
        while (true) {
            List<Channel> channels= epoller.poll();
            for (Channel channel: channels) {
                channel.handleEvenets();
            }
            timerManager.doExpiredTask();
        }
    }
}

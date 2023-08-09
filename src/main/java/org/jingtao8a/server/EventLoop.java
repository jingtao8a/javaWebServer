package org.jingtao8a.server;

import lombok.Getter;
import org.jingtao8a.timer.TimerManager;

import java.nio.channels.SelectionKey;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.*;

@Getter
public class EventLoop {
    private static final long timeSlot = 1000;
    private TimerManager timerManager = new TimerManager();
    private Epoller epoller = new Epoller(timeSlot);

    public Channel register(AbstractSelectableChannel channel) {
        return epoller.register(channel);
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

package org.jingtao8a.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.*;

public class Epoller {
    private Selector selector;
    private EventLoop eventLoop;
    private long timeSlot;//selector wait time

    public Epoller(long timeSlot, EventLoop eventLoop) {
        this.eventLoop = eventLoop;
        this.timeSlot = timeSlot;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Channel> poll() {
        List<Channel> channels= new ArrayList<>();
        int res = 0;
        try {
            res = selector.select(timeSlot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (res > 0) {
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                Channel channel = (Channel)key.attachment();
                channels.add(channel);
            }
        }
        return channels;
    }
    public SelectionKey register(AbstractSelectableChannel channel) {
        try {
            channel.configureBlocking(false);
            return channel.register(selector, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

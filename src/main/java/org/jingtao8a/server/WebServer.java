package org.jingtao8a.server;

import org.jingtao8a.log.Log;
import org.jingtao8a.timer.Timer;
import org.jingtao8a.timer.TimerManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class WebServer {
    private ServerSocketChannel serverSocketChannel;//监听套接字channel
    private Selector selector;//监听器
    private final long timeSlot = 1000;
    private TimerManager timerManager = new TimerManager();
    public WebServer() throws IOException {
        Log.Init("./log", "yuxintao", 500, Log.LogLevel.DEBUG, 10, 3);
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9999));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        timerManager.addTimer(new Timer(new Task(), 3000));
    }

    public void eventLoop() throws IOException {
        while (true) {
            int res = selector.select(timeSlot);
            timerManager.doExpiredTask();
        }
    }
    private class Task implements Runnable {
        @Override
        public void run() {
            Log.Info("%d", System.currentTimeMillis());
            timerManager.addTimer(new Timer(new Task(), 2000));
        }
    }
}

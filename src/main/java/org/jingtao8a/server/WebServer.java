package org.jingtao8a.server;

import org.jingtao8a.log.Log;
import org.jingtao8a.timer.Timer;
import org.jingtao8a.timer.TimerManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

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
    }

    private void dealWithAccept(SelectionKey key) throws IOException {
        SocketChannel clientChannel = ((ServerSocketChannel)key.channel()).accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    private void dealWithRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int count = clientChannel.read(buffer);
        StringBuffer stringBuffer = new StringBuffer();
        if (count > 0) {
            buffer.flip();
            stringBuffer.append(new String(buffer.array(), 0, count));
        } else {
            key.cancel();
            clientChannel.close();
            return;
        }
        String str = stringBuffer.toString();
        Log.Info("%s", str);
        key.attach(str);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void dealWithWrite(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel)key.channel();
        String str = (String)key.attachment();
        key.attach("");
        clientChannel.write(ByteBuffer.wrap(str.getBytes()));
        key.interestOps(SelectionKey.OP_READ);
    }

    public void eventLoop() throws IOException {
        while (true) {
            int res = selector.select(timeSlot);
            timerManager.doExpiredTask();
            if (res > 0) {
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        dealWithAccept(key);
                    } else if (key.isReadable()) {
                        dealWithRead(key);
                    } else if (key.isWritable()) {
                        dealWithWrite(key);
                    }
                }
            }
        }
    }
}

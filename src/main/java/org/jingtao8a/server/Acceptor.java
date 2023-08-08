package org.jingtao8a.server;

import lombok.Getter;
import lombok.Setter;
import org.jingtao8a.Function.NewConnectionCallback;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Setter
@Getter
public class Acceptor {
    private Channel channel;
    private EventLoop eventLoop;
    private int port;
    private NewConnectionCallback newConnectionCallback;
    public Acceptor(EventLoop eventLoop, int port) {
        this.eventLoop = eventLoop;
        this.port  = port;
        bind();
    }

    private void bind() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            SelectionKey selectionKey= eventLoop.getEpoller().register(serverSocketChannel);
            channel = new Channel(selectionKey);
            channel.setChannelAcceptCallback((Channel channel)->{
                try {
                    SocketChannel clientChannel = (SocketChannel)((ServerSocketChannel)channel.getSelectionKey().channel()).accept();
                    SelectionKey clientkey = eventLoop.getEpoller().register(clientChannel);
                    if (newConnectionCallback != null) {
                        newConnectionCallback.run(new TCPConnection(clientkey));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void listen() {
        channel.enableAccept();
    }
}

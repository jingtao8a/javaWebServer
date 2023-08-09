package org.jingtao8a.server;

import lombok.Getter;
import lombok.Setter;
import org.jingtao8a.Function.ChannelAcceptCallback;
import org.jingtao8a.Function.NewConnectionCallback;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.StandardSocketOptions;
import java.net.SocketOptions;

@Setter
@Getter
public class Acceptor {
    private Channel acceptorChannel;
    private EventLoop eventLoop; // 负责监听的EventLoop
    private InetSocketAddress listenSocketAddress; // 监听的地址
    public Acceptor(EventLoop eventLoop, InetSocketAddress listenSocketAddress) {
        this.eventLoop = eventLoop;
        this.listenSocketAddress = listenSocketAddress;
        bind();//绑定地址
    }

    private void bind() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            serverSocketChannel.bind(listenSocketAddress);
            this.acceptorChannel = eventLoop.register(serverSocketChannel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setChannelAcceptCallback(ChannelAcceptCallback channelAcceptCallback) {
        acceptorChannel.setChannelAcceptCallback(channelAcceptCallback);
    }

    public void listen() {//开启监听
        acceptorChannel.enableAccept();
    }
}

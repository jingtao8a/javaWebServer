package org.jingtao8a.reactor;

import lombok.Getter;
import lombok.Setter;
import org.jingtao8a.Function.MessageCallback;
import org.jingtao8a.server.Acceptor;
import org.jingtao8a.server.Channel;
import org.jingtao8a.server.EventLoop;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Getter
@Setter
public class MainReactor {
    private EventLoop eventLoop = new EventLoop();// 负责监听的eventLoop 主线程
    private Thread[] subReactorThreads;
    private Acceptor acceptor;
    private int registerIndex = 0;
    private MessageCallback messageCallback;
    public MainReactor(InetSocketAddress listenSocketAddress, int subReactorNum) {
        subReactorThreads = new Thread[subReactorNum];
        for (int i = 0; i < subReactorNum; ++i) {
            subReactorThreads[i] = new SubReactor();
        }

        acceptor = new Acceptor(eventLoop, listenSocketAddress);
        acceptor.setChannelAcceptCallback((Channel acceptChannel)-> {
            SocketChannel clientSocketChannel = null;
            try {
                clientSocketChannel = (SocketChannel)((ServerSocketChannel)acceptChannel.getSelectionKey().channel()).accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 循环策略:选择将新的SocketChannel注册到某一个subReactors中去
            registerIndex = (registerIndex + 1) % subReactorThreads.length;
            ((SubReactor)subReactorThreads[registerIndex]).register(clientSocketChannel, messageCallback);
        });
    }

    public void start() {
        acceptor.listen();
        for (Thread thread: subReactorThreads) {
            thread.start();
        }
        eventLoop.loop();
    }
}

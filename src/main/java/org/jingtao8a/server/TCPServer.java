package org.jingtao8a.server;

import lombok.Getter;
import lombok.Setter;
import org.jingtao8a.Function.MessageCallback;
import org.jingtao8a.reactor.MainReactor;

import java.net.InetSocketAddress;


public class TCPServer {
    private MainReactor mainReactor;
    private static final int subReactorNum = 10;
    public TCPServer(InetSocketAddress listenSocketAddress) {
        mainReactor = new MainReactor(listenSocketAddress, subReactorNum);
    }

    public void setMessageCallback(MessageCallback messageCallback) {
        mainReactor.setMessageCallback(messageCallback);
    }

    public void start() {
        mainReactor.start();
    }
}

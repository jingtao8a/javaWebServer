package org.jingtao8a.server;
import lombok.Getter;
import lombok.Setter;
import org.jingtao8a.Function.MessageCallback;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

@Setter
@Getter
public class TCPServerSingle {
    private EventLoop eventLoop = new EventLoop();;
    private Acceptor acceptor;
    private List<TCPConnection> tcpConnectionList = new ArrayList<>();
    private MessageCallback messageCallback;
    public TCPServerSingle(InetSocketAddress listenSocketAddress) {
        acceptor = new Acceptor(eventLoop, listenSocketAddress);
        acceptor.setChannelAcceptCallback((Channel acceptChannel) -> {
            SocketChannel clientSocketChannel = null;
            try {
                clientSocketChannel = (SocketChannel)((ServerSocketChannel)acceptChannel.getSelectionKey().channel()).accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Channel clientChannel = eventLoop.register(clientSocketChannel);
            TCPConnection connection = new TCPConnection(clientChannel);
            tcpConnectionList.add(connection);
            connection.setMessageCallBack(messageCallback);
            connection.setCloseConnectionCallback((TCPConnection closeConnection)->{
                tcpConnectionList.remove(closeConnection);
            });
            connection.connectionEstablished();
        });
    }
    public void start() {
        acceptor.listen();
        eventLoop.loop();
    }
}

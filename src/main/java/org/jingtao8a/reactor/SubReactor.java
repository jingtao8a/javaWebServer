package org.jingtao8a.reactor;

import lombok.Getter;
import lombok.Setter;
import org.jingtao8a.Function.MessageCallback;
import org.jingtao8a.http.HTTPHandlerManager;
import org.jingtao8a.server.Channel;
import org.jingtao8a.server.EventLoop;
import org.jingtao8a.server.TCPConnection;

import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SubReactor extends Thread{
    private EventLoop eventLoop = new EventLoop();
    private List<TCPConnection> tcpConnectionList = new ArrayList<>();
    public void register(AbstractSelectableChannel clientSocketChannel, MessageCallback messageCallback) {
        Channel clientChannel = eventLoop.register(clientSocketChannel);
        TCPConnection connection = new TCPConnection(clientChannel);
        tcpConnectionList.add(connection);
        connection.setMessageCallBack(messageCallback);
        connection.setCloseConnectionCallback((TCPConnection closeConnection)->{
            tcpConnectionList.remove(closeConnection);
            HTTPHandlerManager.remove(closeConnection.getUuid());
        });
        connection.connectionEstablished();
    }

    @Override
    public void run() {
        eventLoop.loop();
    }
}

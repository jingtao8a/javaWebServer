package org.jingtao8a.server;
import lombok.Getter;
import lombok.Setter;
import org.jingtao8a.Function.MessageCallback;

import java.util.*;

@Setter
@Getter
public class TCPServerSingle {
    private EventLoop eventLoop = new EventLoop();;
    private Acceptor acceptor;
    private List<TCPConnection> tcpConnectionList = new ArrayList<>();
    private MessageCallback messageCallback;
    public TCPServerSingle(int port) {
        acceptor = new Acceptor(eventLoop, port);
        acceptor.setNewConnectionCallback((TCPConnection connection)->{
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

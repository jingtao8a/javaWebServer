package org.jingtao8a.example;

import org.jingtao8a.server.TCPConnection;
import org.jingtao8a.server.TCPServer;
import org.jingtao8a.server.TCPServerSingle;

import java.nio.ByteBuffer;

public class EchoServer {
    private TCPServer tcpServer = new TCPServer(9999);
    public EchoServer() {
        tcpServer.setMessageCallback((TCPConnection connection, ByteBuffer buffer)-> {
            String str = new String(buffer.array(), 0, buffer.limit());
            System.out.println(str + " ThreadId:" + Thread.currentThread().getId());
            connection.send(str);
        });
        tcpServer.setThreadNum(10);
    }
    public void start() {
        tcpServer.start();
    }
}

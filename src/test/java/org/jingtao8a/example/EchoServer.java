package org.jingtao8a.example;

import org.jingtao8a.server.TCPConnection;
import org.jingtao8a.server.TCPServerSingle;

import java.nio.ByteBuffer;

public class EchoServer {
    private TCPServerSingle tcpServer = new TCPServerSingle(9999);
    public EchoServer() {
        tcpServer.setMessageCallback((TCPConnection connection, ByteBuffer buffer)-> {
            String str = new String(buffer.array(), 0, buffer.limit());
            System.out.println(str);
            connection.send(str);
        });
    }
    public void start() {
        tcpServer.start();
    }
}

package org.jingtao8a.example;

import org.jingtao8a.server.TCPConnection;
import org.jingtao8a.server.TCPServer;
import org.jingtao8a.server.TCPServerSingle;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class EchoServer {
    private TCPServer tcpServer = new TCPServer(new InetSocketAddress("127.0.0.1", 9999));
    public EchoServer() {
        tcpServer.setMessageCallback((TCPConnection connection, ByteBuffer buffer)-> {
            String str = null;
            try {
                str = new String(buffer.array(), 0, buffer.limit(),"GBK");
                System.out.println(str);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            connection.send(str, "GBK");
        });
    }
    public void start() {
        tcpServer.start();
    }
}

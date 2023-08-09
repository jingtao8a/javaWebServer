package org.jingtao8a.server;

import org.jingtao8a.http.HTTPHandlerManager;
import org.jingtao8a.http.HttpHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
public class HTTPServer {
    private TCPServer tcpServer;

    public HTTPServer(InetSocketAddress listenSocketAddress) {
        tcpServer = new TCPServer(listenSocketAddress);
        tcpServer.setMessageCallback((TCPConnection connection, ByteBuffer buffer)->{
            HttpHandler httpHandler = HTTPHandlerManager.get(connection.getUuid());
            String result = httpHandler.process(buffer);
            connection.send(result);
        });
    }
    public void start() {
        tcpServer.start();
    }
}

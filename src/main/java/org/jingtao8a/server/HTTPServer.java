package org.jingtao8a.server;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.jingtao8a.http.HTTPHandlerManager;
import org.jingtao8a.http.HttpHandler;

import javax.sql.DataSource;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Properties;

public class HTTPServer {
    private TCPServer tcpServer;

    public HTTPServer(InetSocketAddress listenSocketAddress) {
        tcpServer = new TCPServer(listenSocketAddress);
        tcpServer.setMessageCallback((TCPConnection connection, ByteBuffer buffer)->{
            HttpHandler httpHandler = HTTPHandlerManager.get(connection.getUuid());
            httpHandler.process(connection, buffer, "UTF-8");
        });
    }

    public void start() {
        tcpServer.start();
    }
}

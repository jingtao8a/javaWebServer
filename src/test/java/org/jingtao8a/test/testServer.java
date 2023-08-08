package org.jingtao8a.test;

import org.jingtao8a.server.TCPServer;
import org.junit.Test;

import java.io.IOException;

public class testServer {
    @Test
    public void testServer() {
        TCPServer tcpServer = new TCPServer(9999);
        tcpServer.start();
    }
}

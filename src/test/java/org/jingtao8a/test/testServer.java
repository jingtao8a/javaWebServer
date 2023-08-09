package org.jingtao8a.test;

import org.jingtao8a.server.TCPServerSingle;
import org.junit.Test;

import java.net.InetSocketAddress;

public class testServer {
    @Test
    public void testServer() {
        TCPServerSingle tcpServer = new TCPServerSingle(new InetSocketAddress("127.0.0.1", 9999));
        tcpServer.start();
    }
}

package org.jingtao8a.test;

import org.jingtao8a.server.TCPServerSingle;
import org.junit.Test;

public class testServer {
    @Test
    public void testServer() {
        TCPServerSingle tcpServer = new TCPServerSingle(9999);
        tcpServer.start();
    }
}

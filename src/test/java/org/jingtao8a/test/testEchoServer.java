package org.jingtao8a.test;

import org.jingtao8a.example.EchoServer;
import org.junit.Test;

public class testEchoServer {
    @Test
    public void testEchoServer() {
        EchoServer echoServer = new EchoServer();
        echoServer.start();
    }
}

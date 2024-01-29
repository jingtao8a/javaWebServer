package org.jingtao8a.test;

import org.jingtao8a.server.HTTPServer;
import org.junit.Test;

import java.net.InetSocketAddress;

public class testHttpServer {
    @Test
    public void testHttpServer() {
        HTTPServer httpServer = new HTTPServer(new InetSocketAddress("127.0.0.1", 7899));
        httpServer.start();
    }
}

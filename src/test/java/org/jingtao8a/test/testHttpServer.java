package org.jingtao8a.test;

import org.jingtao8a.http.HttpHandler;
import org.jingtao8a.server.HTTPServer;
import org.junit.Test;

import java.net.InetSocketAddress;

public class testHttpServer {
    @Test
    public void testHttpServer() {
        HTTPServer httpServer = new HTTPServer(new InetSocketAddress(9999));
        httpServer.start();
    }
}

package org.jingtao8a.test;

import org.jingtao8a.log.Log;
import org.jingtao8a.server.WebServer;
import org.jingtao8a.timer.Timer;
import org.jingtao8a.timer.TimerManager;
import org.junit.Test;

import java.io.IOException;

public class testServer {
    @Test
    public void testServer() throws IOException {
        WebServer webServer = new WebServer();
        webServer.eventLoop();
    }


}

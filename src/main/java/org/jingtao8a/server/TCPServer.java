package org.jingtao8a.server;

import lombok.Getter;
import lombok.Setter;
import org.jingtao8a.Function.MessageCallback;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TCPServer {
    private MessageCallback messageCallback;
    private TCPServerSingle tcpServerSingle;
    private int threadNum = 1;
    private List<Thread> threadList = new ArrayList<>();
    private int port;
    public TCPServer(int port) {
        this.port = port;
    }

    public void start() {
        tcpServerSingle = new TCPServerSingle(port);
        tcpServerSingle.setMessageCallback(messageCallback);
        for (int i = 1; i < threadNum; ++i) {
            threadList.add(new TCPServerSingleThread(i));
        }
        for (Thread thread: threadList) {
            thread.start();
        }
        tcpServerSingle.start();
    }

    private class TCPServerSingleThread extends Thread {
        private int index;
        public TCPServerSingleThread(int index) {
            this.index = index;
        }
        @Override
        public void run() {
            System.out.println("thread "+index);
            TCPServerSingle tcpServerSingle = new TCPServerSingle(port);
            tcpServerSingle.setMessageCallback(messageCallback);
            tcpServerSingle.start();
        }
    }
}

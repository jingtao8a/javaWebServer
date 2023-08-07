package org.jingtao8a.test;

import org.junit.Test;
import org.jingtao8a.log.Log;

import java.io.*;

public class testLog {
    @Test
    public void testLog() {
        try {
            Log.Init("./log", "yuxintao", 3, Log.LogLevel.ERROR, 10, 3);
            for (int i = 0; i < 10; ++i) {
                Log.Info("%s%d", "hello", i);
            }
            for (int i = 0; i < 10; ++i) {
                Log.Error("%s%d", "world", i);
            }
        } finally {
            Log.Flush();
        }
    }

    @Test
    public void testString() {
        String str1 = String.format("%s", "yu");
        String str2 = String.format("%s", "yu");
        System.out.println(str1 == str2);
    }

    @Test
    public void testOS() throws IOException {

        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(new File("./a.txt")));
        os.write("hello");
    }

    @Test
    public void testMultiThreadLog() throws IOException, InterruptedException {
        try {
            Log.Init("./log", "yuxintao", 3, Log.LogLevel.ERROR, 10, 3);
            Thread[] threads = new Thread[3];
            for (int i = 0; i < 3; ++i) {
                threads[i] = new LogThread();
            }
            for (Thread thread: threads) {
                thread.start();
            }
            for (Thread thread: threads) {
                thread.join();
            }
        } finally {
            Log.Flush();
        }
    }

    class LogThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 10; ++i) {
                Log.Error("%s%d", "hello", i);
            }
        }
    }
}

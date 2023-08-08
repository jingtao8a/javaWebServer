package org.jingtao8a.Function;

import org.jingtao8a.server.TCPConnection;

import java.nio.ByteBuffer;

public interface MessageCallback {
    void run(TCPConnection connection, ByteBuffer inputBuffer);
}

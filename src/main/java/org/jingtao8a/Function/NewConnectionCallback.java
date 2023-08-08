package org.jingtao8a.Function;

import org.jingtao8a.server.TCPConnection;

public interface NewConnectionCallback {
    void run(TCPConnection connection);
}

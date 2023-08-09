package org.jingtao8a.http;

import java.nio.ByteBuffer;

public class HttpHandler {
    private ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer outputBuffer = ByteBuffer.allocate(1024);

    public String process(ByteBuffer buffer) {
        return "ok";
    }
}

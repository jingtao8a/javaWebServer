package org.jingtao8a.server;

import lombok.Getter;
import lombok.Setter;
import org.jingtao8a.Function.ChannelReadCallback;
import org.jingtao8a.Function.ChannelWriteCallback;
import org.jingtao8a.Function.CloseConnectionCallback;
import org.jingtao8a.Function.MessageCallback;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

@Setter
@Getter
public class TCPConnection {
    private Channel channel;
    private MessageCallback messageCallBack;
    private CloseConnectionCallback closeConnectionCallback;
    private ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer outputBuffer = ByteBuffer.allocate(1024);
    public TCPConnection(SelectionKey selectionKey) {
        channel = new Channel(selectionKey);
        channel.setChannelReadCallback(new ReadFunction());
        channel.setChannelWriteCallback(new WriteFunciton());
    }
    public void send(String str) {
        SocketChannel socketChannel = (SocketChannel)channel.getSelectionKey().channel();
        outputBuffer.put(str.getBytes(StandardCharsets.UTF_8));
        outputBuffer.flip();
        int res;
        try {
            res = socketChannel.write(outputBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (res == 0 || outputBuffer.hasRemaining()) {
            channel.enableWrite();
        } else {
            outputBuffer.clear();
        }
    }
    public void connectionEstablished() {
        channel.enableRead();
    }
    private class ReadFunction implements ChannelReadCallback {
        @Override
        public void run(Channel channel) {
            SocketChannel clientChannel = (SocketChannel)channel.getSelectionKey().channel();
            int count = 0;
            try {
                count = clientChannel.read(inputBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            StringBuffer stringBuffer = new StringBuffer();
            if (count > 0) {
                inputBuffer.flip();
                stringBuffer.append(new String(inputBuffer.array(), 0, count));
            } else {
                channel.getSelectionKey().cancel();
                try {
                    clientChannel.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (closeConnectionCallback != null) {
                    closeConnectionCallback.run(TCPConnection.this);
                }
                return;
            }
            String str = stringBuffer.toString();
            System.out.println(str);
            if (messageCallBack != null) {
                messageCallBack.run(TCPConnection.this, inputBuffer);
            }
        }
    }

    private class WriteFunciton implements ChannelWriteCallback {
        @Override
        public void run(Channel channel) {
            SocketChannel clientChannel = (SocketChannel)channel.getSelectionKey().channel();
            try {
                clientChannel.write(outputBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!outputBuffer.hasRemaining()) {
                outputBuffer.clear();
                channel.disableWrite();
            }
        }
    }
}

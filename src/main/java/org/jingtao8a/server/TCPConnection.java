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
        if (!channel.isWaitWrite()) { // 当前outputBuffer空闲
            assert(outputBuffer.position() == 0);
            SocketChannel socketChannel = (SocketChannel)channel.getSelectionKey().channel();
            if (str.getBytes().length > outputBuffer.remaining()) {// 超出outputBuffer范围
                outputBuffer = ByteBuffer.allocate(str.getBytes().length);
            }
            outputBuffer.put(str.getBytes());
            outputBuffer.flip();
            try {
                socketChannel.write(outputBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (outputBuffer.hasRemaining()) {//还有剩余数据
                String remainStr = new String(outputBuffer.array(), outputBuffer.position(), outputBuffer.limit());
                outputBuffer = ByteBuffer.allocate(remainStr.getBytes().length);
                outputBuffer.put(remainStr.getBytes());
                outputBuffer.flip();
                channel.enableWrite();
            } else {
                outputBuffer.clear();
            }
        } else {
            String oldStr = new String(outputBuffer.array(), outputBuffer.position(), outputBuffer.limit());
            outputBuffer.clear();
            if (outputBuffer.remaining() < str.getBytes().length) {
                outputBuffer = ByteBuffer.allocate(oldStr.getBytes().length + str.getBytes().length);
            }
            outputBuffer.put(oldStr.getBytes());
            outputBuffer.put(str.getBytes());
            outputBuffer.flip();
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
            boolean close = false;
            try {
                count = clientChannel.read(inputBuffer);
            } catch (IOException e) {
                close = true;
            }
            if (count <= 0 || close) {
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
            inputBuffer.flip();
            if (messageCallBack != null) {
                messageCallBack.run(TCPConnection.this, inputBuffer);
            }
            inputBuffer.clear();
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
            if (!outputBuffer.hasRemaining()) { // 发送完毕
                outputBuffer.clear();
                channel.disableWrite();
            }
        }
    }
}

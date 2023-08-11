package org.jingtao8a.server;

import lombok.Getter;
import lombok.Setter;
import org.jingtao8a.Function.ChannelReadCallback;
import org.jingtao8a.Function.ChannelWriteCallback;
import org.jingtao8a.Function.CloseConnectionCallback;
import org.jingtao8a.Function.MessageCallback;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.locks.StampedLock;

@Setter
@Getter
public class TCPConnection {
    private UUID uuid = UUID.randomUUID();
    private Channel channel;
    private MessageCallback messageCallBack;
    private CloseConnectionCallback closeConnectionCallback;
    private ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer outputBuffer = ByteBuffer.allocate(1024);
    public TCPConnection(Channel channel) {
        this.channel = channel;
        channel.setChannelReadCallback(new ReadFunction());
        channel.setChannelWriteCallback(new WriteFunciton());
    }
    public void send(ByteBuffer buffer) {
        if (!channel.isWaitWrite()) { // 当前outputBuffer空闲
            assert(outputBuffer.position() == 0);
            SocketChannel socketChannel = (SocketChannel)channel.getSelectionKey().channel();
            byte[] bufferArray = buffer.array();
            int offset = buffer.position();
            int count = buffer.remaining();
            if (count > outputBuffer.remaining()) {// 超出outputBuffer范围
                outputBuffer = ByteBuffer.allocate(count * 2);
            }
            outputBuffer.put(bufferArray, offset, count);
            outputBuffer.flip();

            try {
                socketChannel.write(outputBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (outputBuffer.hasRemaining()) {//还有剩余数据
                byte[] remainArray = outputBuffer.array();
                offset = outputBuffer.position();
                count = outputBuffer.remaining();
                outputBuffer = ByteBuffer.allocate(count * 2);
                outputBuffer.put(remainArray, offset, count);
                outputBuffer.flip();
                channel.enableWrite();
            } else {
                outputBuffer.clear();
            }
        } else {
            byte[] oldArray = outputBuffer.array();
            int offset = outputBuffer.position();
            int count = outputBuffer.remaining();
            byte[] newArray = buffer.array();
            outputBuffer.clear();
            int totalLength = count + newArray.length;
            if (outputBuffer.limit() < totalLength) {
                outputBuffer = ByteBuffer.allocate(totalLength * 2);
            }
            outputBuffer.put(oldArray, offset, count);
            outputBuffer.put(newArray);
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

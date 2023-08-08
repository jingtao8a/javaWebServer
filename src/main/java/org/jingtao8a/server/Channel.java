package org.jingtao8a.server;

import lombok.Getter;
import lombok.Setter;
import org.jingtao8a.Function.ChannelAcceptCallback;
import org.jingtao8a.Function.ChannelReadCallback;
import org.jingtao8a.Function.ChannelWriteCallback;

import java.nio.channels.SelectionKey;
@Setter
@Getter
public class Channel { // 对SelectionKey的封装，加上了回调函数
    private ChannelReadCallback channelReadCallback;
    private ChannelWriteCallback channelWriteCallback;
    private ChannelAcceptCallback channelAcceptCallback;
    private SelectionKey selectionKey;
    public void enableRead() { selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ); }
    public void enableWrite() { selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE); }
    public void enableAccept() { selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_ACCEPT); }
    public void disableRead() { selectionKey.interestOps(selectionKey.interestOps() & (~SelectionKey.OP_READ)); }
    public void disableWrite() { selectionKey.interestOps(selectionKey.interestOps() & (~SelectionKey.OP_WRITE)); }
    public void disableAccept() { selectionKey.interestOps(selectionKey.interestOps() & (~SelectionKey.OP_ACCEPT)); }
    public Channel(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
        selectionKey.attach(this);
    }
    public void handleEvenets() {
        if (selectionKey.isAcceptable()) {
            channelAcceptCallback.run(this);
        } else if (selectionKey.isWritable()) {
            channelWriteCallback.run(this);
        } else if (selectionKey.isReadable()) {
            channelReadCallback.run(this);
        }
    }
}

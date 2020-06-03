package com.mike.rpc.api.net.channel;

import io.netty.channel.Channel;

public class ChannelHandle {
    private long channelId;
    private Channel channel;

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}

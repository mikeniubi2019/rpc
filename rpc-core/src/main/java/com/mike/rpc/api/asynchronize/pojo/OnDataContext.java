package com.mike.rpc.api.asynchronize.pojo;

import io.netty.channel.ChannelHandlerContext;

public abstract class OnDataContext <T> {
    private T t;
    private ChannelHandlerContext channelHandlerContext;

    public OnDataContext(T t, ChannelHandlerContext channelHandlerContext) {
        this.t = t;
        this.channelHandlerContext = channelHandlerContext;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
}

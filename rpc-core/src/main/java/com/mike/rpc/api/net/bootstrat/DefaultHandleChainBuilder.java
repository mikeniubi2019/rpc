package com.mike.rpc.api.net.bootstrat;

import com.mike.rpc.api.net.channel.DymicChannelInitializer;
import com.mike.rpc.api.utils.Factory.MarshallingFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

import java.util.ArrayList;
import java.util.List;

public class DefaultHandleChainBuilder implements HandleChainBuilder{
    private List<ChannelHandler> handlerChains = new ArrayList<>();
    private List<ChannelHandler> userHandles = new ArrayList<>();
    public DefaultHandleChainBuilder addCodecHandler(ChannelHandler channelHandler){
        if (!handlerChains.contains(channelHandler)){
            handlerChains.add(channelHandler);
        }
        return this;
    }

    public DefaultHandleChainBuilder addUserHandler(ChannelHandler channelHandler){
        if (!userHandles.contains(channelHandler)){
            userHandles.add(channelHandler);
        }
        return this;
    }

    @Override
    public void doBuildHandlerChain() {

    }

    @Override
    public ChannelInitializer build() {
        return new DymicChannelInitializer(handlerChains,userHandles);
    }

    public List<ChannelHandler> getHandlerChains() {
        return handlerChains;
    }

    public void setHandlerChains(List<ChannelHandler> handlerChains) {
        this.handlerChains = handlerChains;
    }

    public List<ChannelHandler> getUserHandles() {
        return userHandles;
    }

    public void setUserHandles(List<ChannelHandler> userHandles) {
        this.userHandles = userHandles;
    }
}

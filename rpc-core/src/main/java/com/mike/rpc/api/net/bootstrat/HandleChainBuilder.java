package com.mike.rpc.api.net.bootstrat;

import io.netty.channel.ChannelInitializer;

public interface HandleChainBuilder {
    void doBuildHandlerChain();
    ChannelInitializer build();
}

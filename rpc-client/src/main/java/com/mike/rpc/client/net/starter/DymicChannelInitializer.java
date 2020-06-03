package com.mike.rpc.client.net.starter;

import com.mike.rpc.api.utils.Factory.MarshallingFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

import java.util.List;

public class DymicChannelInitializer extends ChannelInitializer{

    private List<ChannelHandler> handlerChains;
    private List<ChannelHandler> userHandles;

    public DymicChannelInitializer(List<ChannelHandler> handlerChains, List<ChannelHandler> userHandles) {
        this.handlerChains = handlerChains;
        this.userHandles = userHandles;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline channelPipeline = channel.pipeline();
        if (handlerChains.size()<1){
            channelPipeline.addLast(MarshallingFactory.getMarshallingDecoder());
            channelPipeline.addLast(MarshallingFactory.getMarshallingEncode());
        }else {
            for (ChannelHandler channelHandler : handlerChains){
                channelPipeline.addLast(channelHandler);
            }
        }
        if (!userHandles.isEmpty()){
            for (ChannelHandler channelHandler : userHandles){
                channelPipeline.addLast(channelHandler);
            }
        }
    }
}

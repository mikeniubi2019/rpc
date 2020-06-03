package com.mike.rpc.client.core.pojo;

import com.mike.rpc.api.asynchronize.pojo.OnDataContext;
import com.mike.rpc.api.net.pojo.RpcRequest;
import com.mike.rpc.api.net.pojo.RpcResponse;
import io.netty.channel.ChannelHandlerContext;

public class RpcResponseHandlerContext extends OnDataContext<RpcResponse> {

    public RpcResponseHandlerContext(RpcResponse o, ChannelHandlerContext channelHandlerContext) {
        super(o, channelHandlerContext);
    }

}

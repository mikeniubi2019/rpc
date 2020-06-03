package com.mike.rpc.serve.core.pojo;

import com.mike.rpc.api.asynchronize.pojo.OnDataContext;
import com.mike.rpc.api.net.pojo.RpcRequest;
import io.netty.channel.ChannelHandlerContext;

public class RpcRequestHandlerContext extends OnDataContext<RpcRequest> {

    public RpcRequestHandlerContext(RpcRequest o, ChannelHandlerContext channelHandlerContext) {
        super(o, channelHandlerContext);
    }

}

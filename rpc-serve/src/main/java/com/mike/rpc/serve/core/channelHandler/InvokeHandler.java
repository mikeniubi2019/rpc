package com.mike.rpc.serve.core.channelHandler;


import com.google.common.util.concurrent.RateLimiter;
import com.mike.rpc.api.net.pojo.RpcRequest;

import com.mike.rpc.serve.asychroniz.ServeAsychronizeHandlerContex;
import com.mike.rpc.serve.contex.AnnotationContex;
import com.mike.rpc.serve.core.pojo.RpcRequestHandlerContext;
import com.mike.rpc.serve.utils.ContexUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


@ChannelHandler.Sharable
public class InvokeHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private RateLimiter rateLimiter = RateLimiter.create(3000);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {

        //限流
        rateLimiter.acquire();
        //TODO 放入disruptor
        AnnotationContex annotationContex = (AnnotationContex) ContexUtils.getCurrentContex();
        RpcRequestHandlerContext rpcRequestHandlerContext = new RpcRequestHandlerContext((RpcRequest) rpcRequest.clone(),channelHandlerContext);

        ServeAsychronizeHandlerContex serveAsychronizeHandlerContex = (ServeAsychronizeHandlerContex)annotationContex.getAsychronizeHandler();


        serveAsychronizeHandlerContex.onData(rpcRequestHandlerContext);
    }
}

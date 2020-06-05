package com.mike.rpc.serve.core.channelHandler;


import com.google.common.util.concurrent.RateLimiter;
import com.mike.rpc.api.net.pojo.RpcRequest;

import com.mike.rpc.api.net.pojo.RpcResponse;
import com.mike.rpc.serve.asychroniz.ServeAsychronizeHandlerContex;
import com.mike.rpc.serve.contex.AnnotationContex;
import com.mike.rpc.serve.core.pojo.RpcRequestHandlerContext;
import com.mike.rpc.serve.utils.ContexUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


@ChannelHandler.Sharable
public class InvokeHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private RateLimiter rateLimiter = RateLimiter.create(1000);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {

        //限流
        if (!rateLimiter.tryAcquire()){
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setId(rpcRequest.getId());
            rpcResponse.setSuccess(false);
            rpcResponse.setVersion(rpcRequest.getVersion());
            rpcResponse.setMessage("系统限流，请稍后再试！");
            channelHandlerContext.writeAndFlush(rpcResponse);
            return;
        }
        //TODO 放入disruptor
        AnnotationContex annotationContex = (AnnotationContex) ContexUtils.getCurrentContex();
        RpcRequestHandlerContext rpcRequestHandlerContext = new RpcRequestHandlerContext((RpcRequest) rpcRequest.clone(),channelHandlerContext);

        ServeAsychronizeHandlerContex serveAsychronizeHandlerContex = (ServeAsychronizeHandlerContex)annotationContex.getAsychronizeHandler();


        serveAsychronizeHandlerContex.onData(rpcRequestHandlerContext);
    }
}

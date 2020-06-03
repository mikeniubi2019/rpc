package com.mike.rpc.client.channelHandler;

import com.mike.rpc.api.net.pojo.RpcResponse;
import com.mike.rpc.client.asychroniz.ClientAsychronizeHandlerContex;
import com.mike.rpc.client.contex.AnnotationContex;
import com.mike.rpc.client.core.pojo.RpcResponseHandlerContext;
import com.mike.rpc.client.utils.ContexUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
@ChannelHandler.Sharable
public class SendRequestHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        //放入disruptor
        RpcResponseHandlerContext rpcResponseHandlerContext = new RpcResponseHandlerContext(rpcResponse,channelHandlerContext);
        AnnotationContex annotationContex = (AnnotationContex) ContexUtils.getCurrentContex();
        ClientAsychronizeHandlerContex clientAsychronizeHandlerContex = (ClientAsychronizeHandlerContex)annotationContex.getAsychronizeHandler();
        clientAsychronizeHandlerContex.onData(rpcResponseHandlerContext);
    }
}

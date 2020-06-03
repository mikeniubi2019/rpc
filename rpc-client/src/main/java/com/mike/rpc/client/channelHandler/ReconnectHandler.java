package com.mike.rpc.client.channelHandler;

import com.mike.rpc.client.contex.AbstractContex;
import com.mike.rpc.client.net.pojo.IpChannelHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;

@ChannelHandler.Sharable
public class ReconnectHandler extends ChannelInboundHandlerAdapter {
    private AbstractContex abstractContex;
    public ReconnectHandler(AbstractContex abstractContex) {
        this.abstractContex = abstractContex;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleState){
            String add = ctx.channel().remoteAddress().toString();
            String adds[] = add.split(":");
            String tempPath ;
            IpChannelHolder ipChannelHolder = null;
            if (adds[0].contains("/")){
                tempPath = adds[0].split("/")[0];
                ipChannelHolder = this.abstractContex.getIpStrMappingChannelHolder().get(tempPath+":"+adds[1]);
                if (ipChannelHolder ==null){
                    ipChannelHolder = this.abstractContex.getIpStrMappingChannelHolder().get(adds[0].split("/")[1]+":"+adds[1]);
                }
            }else {
                ipChannelHolder = this.abstractContex.getIpStrMappingChannelHolder().get(add);
            }
            if (ipChannelHolder ==null) throw new Exception("ipChannelHolder为空 ");
            //如果开启了回退，则直接跳过,不进行重试
            if (ipChannelHolder.isFallback()){
                return;
            }
            //重试
            Channel channel = ipChannelHolder.getBootstrat().reConnecting(ipChannelHolder.getAddr(),ipChannelHolder.getPort());
            if (channel!=null) {
                ipChannelHolder.setChannel(channel);
            }else {
                System.out.println("心跳设置回退方法");
                ipChannelHolder.setFallback(true);
                ipChannelHolder.getChannel().close();
            }
        }
        else {
            ctx.fireUserEventTriggered(evt);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        for (StackTraceElement stackTraceElement : cause.getStackTrace()){
            System.out.println(stackTraceElement.toString());
        }
        ctx.fireExceptionCaught(cause);
    }
}

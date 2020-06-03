package com.mike.rpc.client.net.starter;

import com.mike.rpc.api.net.bootstrat.Bootstrat;
import com.mike.rpc.client.channelHandler.SendRequestHandler;
import com.mike.rpc.client.contex.AbstractContex;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class ClientBootstrat implements Bootstrat {
    private NioEventLoopGroup worker;
    private Bootstrap bootstrap;
    private ChannelInitializer channelInitializer;
    private AbstractContex abstractContex;


    public void innitClient() {
        worker = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                //.option(ChannelOption.TCP_NODELAY, true)
                .handler(channelInitializer);
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {
        if (worker != null) worker.shutdownGracefully();
    }

    public ClientBootstrat(AbstractContex abstractContex) {
        this.abstractContex = abstractContex;
    }

    public ChannelInitializer getChannelInitializer() {
        return channelInitializer;
    }

    public void setChannelInitializer(ChannelInitializer channelInitializer) {
        //生成channelHandler
        this.channelInitializer = channelInitializer;
    }

    public Channel connect(String remotAdd, int port) {
        Channel channel = null;
        try {
            System.out.println(remotAdd + ":" + port + "已经连接!" + this + "----" + this.channelInitializer);
            channel = bootstrap.connect(remotAdd, port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public Channel reConnecting(String remotAdd, int port) {
        int count = 0;
        boolean flage = false;
        Channel channel = null;
        while (count < 3 || !flage) {
            try {
                channel = this.connect(remotAdd, port);
            } catch (Exception e) {
                count = count + 1;
            }
            flage = !flage;
        }
        return channel;
    }

    ;
}

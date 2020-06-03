package com.mike.rpc.serve.net.starter;

import com.mike.rpc.api.net.bootstrat.Bootstrat;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class ServeBootstrat implements Bootstrat {
    private String localAdd;
    private int port;
    private NioEventLoopGroup boss ;
    private NioEventLoopGroup worker ;
    private ServerBootstrap serverBootstrap ;
    private ChannelInitializer channelInitializer;
    private ChannelFuture channelFuture;


    @Override
    public void start() {

        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024*1024)
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(channelInitializer);

        try {
            System.out.println("已经启动duankou:"+localAdd+port);
            channelFuture = serverBootstrap.bind(localAdd,port).sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        try {
            channelFuture.channel().close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (boss!=null) boss.shutdownGracefully();
            if (worker!=null) worker.shutdownGracefully();
            System.out.println("结束了");
        }
    }

    public ServeBootstrat(String localAdd, int port) {
        this.localAdd = localAdd;
        this.port = port;
    }

    public ChannelInitializer getChannelInitializer() {
        return channelInitializer;
    }

    public void setChannelInitializer(ChannelInitializer channelInitializer) {
        this.channelInitializer = channelInitializer;
    }
}

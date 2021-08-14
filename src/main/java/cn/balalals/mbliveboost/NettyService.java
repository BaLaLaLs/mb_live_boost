package cn.balalals.mbliveboost;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;

@ApplicationScoped
public class NettyService {
    public static NioEventLoopGroup mainGroup;
    public static NioEventLoopGroup subGroup;

    void startup(@Observes StartupEvent event) {
        // 一个主线程组(用于监听新连接并初始化通道)，一个分发线程组(用于IO事件的处理)
        NettyService.mainGroup = new NioEventLoopGroup(1);
        NettyService.subGroup = new NioEventLoopGroup();
        ServerBootstrap sb = new ServerBootstrap();
        try {
            sb.group(NettyService.mainGroup, NettyService.subGroup)
                    .channel(NioServerSocketChannel.class)
                    // 这里是一个自定义的通道初始化器，用来添加编解码器和处理器
                    .childHandler(new WsChannelInitializer());
            sb.bind(8989).sync().addListener(r -> {
                if (r.isSuccess()) {
                    System.out.println("监听成功！");
                } else {
                    System.out.println("监听失败");
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            stopService();
        }
    }
    public void stopService() {
        // 释放资源
        NettyService.mainGroup.shutdownGracefully();
        NettyService.subGroup.shutdownGracefully();
    }

    static class WsChannelInitializer extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel channel) {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(new HttpServerCodec())
                    .addLast(new ChunkedWriteHandler())
                    .addLast(new HttpObjectAggregator(1024 * 64));
            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
            // 自定义的处理器
            pipeline.addLast(new WsServerHandler());
        }
    }
}

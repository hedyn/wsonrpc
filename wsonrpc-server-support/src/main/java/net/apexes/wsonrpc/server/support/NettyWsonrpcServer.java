/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server.support;

import java.util.concurrent.atomic.AtomicBoolean;

import net.apexes.wsonrpc.core.WsonrpcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import net.apexes.wsonrpc.server.WsonrpcServer;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class NettyWsonrpcServer {
    
    private static final Logger LOG = LoggerFactory.getLogger(NettyWsonrpcServer.class);

    private final int port;
    private final String urlPath;
    private final NettyWsonrpcHandler wsonrpcHandler;
    private ChannelFuture future;
    private volatile AtomicBoolean isclose = new AtomicBoolean(false);

    public NettyWsonrpcServer(int port, String urlPath, WsonrpcConfig config) {
        this.port = port;
        this.urlPath = urlPath;
        this.wsonrpcHandler = new NettyWsonrpcHandler(config);
    }
    
    public WsonrpcServer getWsonrpcServer() {
        return wsonrpcHandler.getWsonrpcServer();
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WsonrpcChannelInitializer(urlPath, wsonrpcHandler))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        LOG.debug("Starting. port={}, urlPath={}", port, urlPath);

        isclose.set(false);
        try {
            future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            if (!isclose.get()) {
                LOG.error("", e);
            }
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    
    public void stop() {
        isclose.set(true);
        future.cancel(true);
    }

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WsonrpcChannelInitializer extends ChannelInitializer<SocketChannel> {

        private final String urlPath;
        private final NettyWsonrpcHandler handler;

        private WsonrpcChannelInitializer(String urlPath, NettyWsonrpcHandler handler) {
            this.urlPath = urlPath;
            this.handler = handler;
        }

        @Override
        public void initChannel(SocketChannel ch) throws Exception {// 2
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
            pipeline.addLast(new ChunkedWriteHandler());
            // pipeline.addLast(new HttpRequestHandler("/ws"));
            pipeline.addLast(new WebSocketServerProtocolHandler(urlPath));
            pipeline.addLast(handler);
        }
    }

}

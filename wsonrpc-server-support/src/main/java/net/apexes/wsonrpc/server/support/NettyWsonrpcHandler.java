/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server.support;

import java.io.IOException;
import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.apexes.wsonrpc.core.WsonrpcConfig;
import net.apexes.wsonrpc.core.WebSocketSession;
import net.apexes.wsonrpc.server.WsonrpcServer;
import net.apexes.wsonrpc.server.WsonrpcServerBase;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
@Sharable
public class NettyWsonrpcHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    
//    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final WsonrpcServerBase serverBase;
    
    public NettyWsonrpcHandler(WsonrpcConfig config) {
        serverBase = new WsonrpcServerBase(config);
    }
    
    public WsonrpcServer getWsonrpcServer() {
        return serverBase;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if (msg instanceof BinaryWebSocketFrame) {
            Channel channel = ctx.channel();
            BinaryWebSocketFrame binnaryMsg = (BinaryWebSocketFrame) msg;
            ByteBuf buf = binnaryMsg.content();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            serverBase.onMessage(sessionId(channel), ByteBuffer.wrap(bytes));
        } else if (msg instanceof PingWebSocketFrame) {
            Channel channel = ctx.channel();
            channel.writeAndFlush(new PongWebSocketFrame());
        }
    }
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    }
        
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelWebSocketSessionAdapter session = new ChannelWebSocketSessionAdapter(ctx);
//        channels.add(session.channel);
        serverBase.onOpen(session);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        serverBase.onClose(sessionId(channel));
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        serverBase.onError(sessionId(channel), cause);
    }
    
    private static String sessionId(Channel channel) {
        if (channel == null) {
            return null;
        }
        return Integer.toHexString(System.identityHashCode(channel));
    }

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class ChannelWebSocketSessionAdapter implements WebSocketSession {
        
        private final ChannelHandlerContext ctx;
        private final Channel channel;
        private final String id;
        
        private ChannelWebSocketSessionAdapter(ChannelHandlerContext ctx) {
            this.ctx = ctx;
            this.channel = ctx.channel();
            this.id = sessionId(channel);
        }

        @Override
        public void sendBinary(byte[] bytes) throws IOException {
            channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes)));
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean isOpen() {
            return channel.isOpen() && channel.isActive();
        }

        @Override
        public void ping() throws IOException {
            channel.writeAndFlush(new PingWebSocketFrame());
        }

        @Override
        public void close() throws IOException {
            ctx.close();
        }
    }

}

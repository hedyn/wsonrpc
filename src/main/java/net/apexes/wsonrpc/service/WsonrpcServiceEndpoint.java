package net.apexes.wsonrpc.service;

import java.nio.ByteBuffer;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public abstract class WsonrpcServiceEndpoint {

    protected final WsonrpcServiceEndpointProxy proxy;

    protected WsonrpcServiceEndpoint(WsonrpcConfig config) {
        proxy = new WsonrpcServiceEndpointProxy(config);
    }

    public WsonrpcServiceEndpoint addService(String name, Object handler) {
        proxy.addService(name, handler);
        return this;
    }

    public void setExceptionProcessor(ExceptionProcessor processor) {
        proxy.setExceptionProcessor(processor);
    }

    @OnOpen
    public void onOpen(Session session) {
        proxy.onOpen(session);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        proxy.onClose(session);
    }

    @OnMessage
    public void onMessage(final Session session, final ByteBuffer buffer) {
        proxy.onMessage(session, buffer);
    }

}

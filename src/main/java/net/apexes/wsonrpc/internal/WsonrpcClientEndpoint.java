/**
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.internal;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import net.apexes.wsonrpc.ClientStatusListener;
import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WebsocketConnector;
import net.apexes.wsonrpc.WsonrpcClient;
import net.apexes.wsonrpc.WsonrpcConfig;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
@ClientEndpoint
public class WsonrpcClientEndpoint extends WsonrpcEndpoint implements WsonrpcClient {

    private final WebsocketConnector connector;
    private final URI uri;
    private final WsonrpcDispatcher dispatcher;
    private final List<ClientStatusListener> statusListenerList;

    public WsonrpcClientEndpoint(URI uri, WsonrpcConfig config, WebsocketConnector connector) {
        this.uri = uri;
        this.connector = connector;
        this.statusListenerList = new CopyOnWriteArrayList<>();
        this.dispatcher = new WsonrpcDispatcher(config);
    }

//    public void setExceptionResolver(ExceptionResolver exceptionResolver) {
//        dispatcher.setExceptionResolver(exceptionResolver);
//    }

    @Override
    public void setExceptionProcessor(ExceptionProcessor processor) {
        dispatcher.setExceptionProcessor(processor);
    }

    @Override
    public void addStatusListener(ClientStatusListener listener) {
        statusListenerList.add(listener);
    }

    @Override
    public void removeStatusListener(ClientStatusListener listener) {
        statusListenerList.remove(listener);
    }

    @Override
    public WsonrpcClient addService(String name, Object handler) {
        dispatcher.addService(name, handler);
        return this;
    }

    @Override
    public void connect() throws Exception {
        if (!isOnline()) {
            connector.connectToServer(this, uri);
        }
    }

    private synchronized void fireOpen() {
        for (ClientStatusListener listener : statusListenerList) {
            listener.onOpen();
        }
    }

    private synchronized void fireClose() {
        for (ClientStatusListener listener : statusListenerList) {
            listener.onClose();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        online(session, dispatcher);
        fireOpen();
    }

    @OnMessage
    public void onMessage(Session session, ByteBuffer buffer) {
        try {
            dispatcher.handleMessage(session, buffer);
        } catch (Throwable throwable) {
            onError(session, throwable);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (dispatcher.getExceptionProcessor() != null) {
            dispatcher.getExceptionProcessor().onError(throwable);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        fireClose();
    }

}

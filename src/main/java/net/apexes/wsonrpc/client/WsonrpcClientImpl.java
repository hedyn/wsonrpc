/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import java.io.IOException;
import java.net.URI;

import net.apexes.jsonrpc.ServiceRegistry;
import net.apexes.wsonrpc.ErrorProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.internal.WsonrpcDispatcher;
import net.apexes.wsonrpc.internal.WsonrpcEndpoint;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */

class WsonrpcClientImpl extends WsonrpcEndpoint implements WsonrpcClient, WsonrpcClientEndpoint {

    private final WebsocketConnector connector;
    private final URI uri;
    private final WsonrpcDispatcher dispatcher;
    private WsonrpcClientListener clientListener;
    
    public WsonrpcClientImpl(URI uri, WsonrpcConfig config, WebsocketConnector connector) {
        this.uri = uri;
        this.connector = connector;
        this.dispatcher = new WsonrpcDispatcher(config);
    }
    
    @Override
    public ServiceRegistry getServiceRegistry() {
        return dispatcher.getServiceRegistry();
    }
    
    @Override
    public long getTimeout() {
        return dispatcher.getTimeout();
    }

    @Override
    public void setClientListener(WsonrpcClientListener listener) {
        this.clientListener = listener;
        if (clientListener != null) {
            dispatcher.setErrorProcessor(new ErrorProcessorAdapter(clientListener));
        } else {
            dispatcher.setErrorProcessor(null);
        }
    }

    @Override
    public void connect() throws Exception {
        if (!isConnected()) {
            connector.connectToServer(this, uri, getTimeout());
        }
    }

    @Override
    public void onOpen(final WsonrpcSession session) {
        WsonrpcSession sessionProxy = new WsonrpcSession() {

            @Override
            public String getId() {
                return session.getId();
            }

            @Override
            public boolean isOpen() {
                return session.isOpen();
            }

            @Override
            public void sendBinary(byte[] bytes) throws IOException {
                session.sendBinary(bytes);
                fireSentMessage(bytes);
            }

            @Override
            public void ping() throws IOException {
                session.ping();
                fireSentPing();
            }

            @Override
            public void close() throws IOException {
                session.close();
            }
            
        };
        online(sessionProxy, dispatcher);
        fireOpen();
    }

    @Override
    public void onMessage(byte[] data) {
        try {
            dispatcher.handleMessage(getSession(), data);
        } catch (Throwable throwable) {
            onError(throwable);
        }
    }

    @Override
    public void onError(Throwable error) {
        if (dispatcher.getErrorProcessor() != null) {
            dispatcher.getErrorProcessor().onError(getSessionId(), error);
        }
    }

    @Override
    public void onClose(int code, String reason) {
        offline();
        fireClose(code, reason);
    }

    private synchronized void fireOpen() {
        if (clientListener != null) {
            clientListener.onOpen(this);
        }
    }

    private synchronized void fireClose(int code, String reason) {
        if (clientListener != null) {
            clientListener.onClose(this, code, reason);
        }
    }
    
    private synchronized void fireSentMessage(byte[] bytes) {
        if (clientListener != null) {
            clientListener.onSentMessage(bytes);
        }
    }
    
    private synchronized void fireSentPing() {
        if (clientListener != null) {
            clientListener.onSentPing();
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class ErrorProcessorAdapter implements ErrorProcessor {
        
        private final WsonrpcClientListener listener;
        
        ErrorProcessorAdapter(WsonrpcClientListener listener) {
            this.listener = listener;
        }

        @Override
        public void onError(String sessionId, Throwable error) {
            listener.onError(error);
        }
        
    }
    
}

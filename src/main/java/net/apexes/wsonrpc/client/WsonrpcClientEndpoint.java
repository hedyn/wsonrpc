package net.apexes.wsonrpc.client;

import java.net.URI;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.internal.WsonrpcDispatcher;
import net.apexes.wsonrpc.internal.WsonrpcEndpoint;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */

public class WsonrpcClientEndpoint extends WsonrpcEndpoint implements WsonrpcClient {

    private final WebsocketConnector connector;
    private final URI uri;
    private final WsonrpcDispatcher dispatcher;
    private ClientStatusListener statusListener;
    
    public WsonrpcClientEndpoint(URI uri, WsonrpcConfig config, WebsocketConnector connector) {
        this.uri = uri;
        this.connector = connector;
        this.dispatcher = new WsonrpcDispatcher(config);
    }

    @Override
    public void addService(String name, Object handler) {
        dispatcher.addService(name, handler);
    }

    @Override
    public void setExceptionProcessor(ExceptionProcessor processor) {
        dispatcher.setExceptionProcessor(processor);
    }

    @Override
    public ExceptionProcessor getExceptionProcessor() {
        return dispatcher.getExceptionProcessor();
    }

    @Override
    public void setStatusListener(ClientStatusListener listener) {
        this.statusListener = listener;
    }

    @Override
    public void connect() throws Exception {
        if (!isOnline()) {
            connector.connectToServer(this, uri);
        }
    }

    @Override
    public void onOpen(WsonrpcSession session) {
        online(session, dispatcher);
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
        if (dispatcher.getExceptionProcessor() != null) {
            dispatcher.getExceptionProcessor().onError(error);
        }
    }

    @Override
    public void onClose(int code, String reason) {
        offline();
        fireClose();
    }

    private synchronized void fireOpen() {
        if (statusListener != null) {
            statusListener.onOpen(WsonrpcClientEndpoint.this);
        }
    }

    private synchronized void fireClose() {
        if (statusListener != null) {
            statusListener.onClose(WsonrpcClientEndpoint.this);
        }
    }
}

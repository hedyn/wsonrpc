package net.apexes.wsonrpc.client;

import java.net.URI;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.client.support.TyrusWebsocketConnector;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcClient extends WsonrpcRemote {

    void connect() throws Exception;

    WsonrpcClient addService(String name, Object handler);

    void setExceptionProcessor(ExceptionProcessor processor);

    void addStatusListener(ClientStatusListener listener);

    void removeStatusListener(ClientStatusListener listener);
    
    void onOpen(WsonrpcSession session);

    void onMessage(byte[] bytes);

    void onError(Throwable throwable);

    void onClose(int code, String reason);

    /**
     * 
     * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
     *
     */
    public static class Builder {

        public static WsonrpcClient create(URI uri, WsonrpcConfig config) {
            return create(uri, config, new TyrusWebsocketConnector());
        }

        public static WsonrpcClient create(URI uri, WsonrpcConfig config, WebsocketConnector connector) {
            return new WsonrpcClientEndpoint(uri, config, connector);
        }
    }

}

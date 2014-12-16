package net.apexes.wsonrpc;

import java.net.URI;

import net.apexes.wsonrpc.internal.TyrusWebsocketConnector;
import net.apexes.wsonrpc.internal.WsonrpcClientEndpoint;

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

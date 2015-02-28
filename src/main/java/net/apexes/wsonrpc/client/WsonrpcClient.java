package net.apexes.wsonrpc.client;

import java.net.URI;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.ServiceRegistry;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.client.support.SimpleWebsocketConnector;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcClient extends WsonrpcRemote {
    
    ServiceRegistry getServiceRegistry();
    
    ExceptionProcessor getExceptionProcessor();
    
    void setExceptionProcessor(ExceptionProcessor processor);

    void setStatusListener(ClientStatusListener listener);

    /**
     * 连接服务端，在连接上之前调用此方法的线程都将阻塞
     */
    void connect() throws Exception;
    
    void onOpen(WsonrpcSession session);

    void onMessage(byte[] data);

    void onError(Throwable error);

    void onClose(int code, String reason);

    /**
     * 
     * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
     *
     */
    final class Builder {
        
        public static Builder create(URI uri) {
            return new Builder(uri);
        }
        
        private final URI uri;
        private WebsocketConnector connector;

        private Builder(URI uri) {
            this.uri = uri;
        }

        public Builder connector(WebsocketConnector connector) {
            this.connector = connector;
            return this;
        }
        
        public WsonrpcClient build(WsonrpcConfig config) {
            if (connector == null) {
                connector = new SimpleWebsocketConnector();
            }
            return new WsonrpcClientEndpoint(uri, config, connector);
        }
        
    }

}

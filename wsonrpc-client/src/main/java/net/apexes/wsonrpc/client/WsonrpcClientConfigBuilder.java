package net.apexes.wsonrpc.client;

import net.apexes.wsonrpc.client.support.SimpleWebsocketConnector;
import net.apexes.wsonrpc.core.*;
import net.apexes.wsonrpc.json.JsonImplementor;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 */
public class WsonrpcClientConfigBuilder {

    public static WsonrpcClientConfigBuilder create() {
        return new WsonrpcClientConfigBuilder();
    }

    private final WsonrpcConfigBuilder builder;
    private WebsocketConnector connector;
    private int connectTimeout;

    private WsonrpcClientConfigBuilder() {
        builder = WsonrpcConfigBuilder.create();
    }

    /**
     *
     * @param jsonImpl
     * @return
     */
    public WsonrpcClientConfigBuilder json(JsonImplementor jsonImpl) {
        builder.json(jsonImpl);
        return this;
    }

    /**
     *
     * @param binaryWrapper
     * @return
     */
    public WsonrpcClientConfigBuilder binaryWrapper(BinaryWrapper binaryWrapper) {
        builder.binaryWrapper(binaryWrapper);
        return this;
    }

    /**
     *
     * @param errorProcessor
     * @return
     */
    public WsonrpcClientConfigBuilder errorProcessor(WsonrpcErrorProcessor errorProcessor) {
        builder.errorProcessor(errorProcessor);
        return this;
    }

    /**
     *
     * @param connector
     * @return
     */
    public WsonrpcClientConfigBuilder connector(WebsocketConnector connector) {
        this.connector = connector;
        return this;
    }

    /**
     *
     * @param connectTimeout
     * @return
     */
    public WsonrpcClientConfigBuilder connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public WsonrpcClient client(String url) throws URISyntaxException {
        if (connector == null) {
            connector = new SimpleWebsocketConnector();
        }
        URI uri = new URI(url);
        WsonrpcClientConfig config = new WsonrpcClientConfigImpl(builder.build(), uri, connector, connectTimeout);
        return new WsonrpcClientImpl(config);
    }

    /**
     *
     * @param client
     * @return
     */
    public RemoteInvoker invoker(WsonrpcClient client) {
        return RemoteInvoker.create(client);
    }

    /**
     *
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WsonrpcClientConfigImpl implements WsonrpcClientConfig {

        private final WsonrpcConfig config;
        private final URI uri;
        private final WebsocketConnector connector;
        private final int connectTimeout;

        private WsonrpcClientConfigImpl(WsonrpcConfig config, URI uri, WebsocketConnector connector, int connectTimeout) {
            this.config = config;
            this.uri = uri;
            this.connector = connector;
            this.connectTimeout = connectTimeout;
        }

        @Override
        public JsonImplementor getJsonImplementor() {
            return config.getJsonImplementor();
        }

        @Override
        public BinaryWrapper getBinaryWrapper() {
            return config.getBinaryWrapper();
        }

        @Override
        public WsonrpcExecutor getWsonrpcExecutor() {
            return config.getWsonrpcExecutor();
        }

        @Override
        public WsonrpcErrorProcessor getErrorProcessor() {
            return config.getErrorProcessor();
        }

        @Override
        public URI getURI() {
            return uri;
        }

        @Override
        public WebsocketConnector getWebsocketConnector() {
            return connector;
        }

        @Override
        public int getConnectTimeout() {
            return connectTimeout;
        }

    }

}

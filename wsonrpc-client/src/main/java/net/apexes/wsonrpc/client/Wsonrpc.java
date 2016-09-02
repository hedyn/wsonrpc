/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;

import net.apexes.wsonrpc.client.support.SimpleWebsocketConnector;
import net.apexes.wsonrpc.core.BinaryWrapper;
import net.apexes.wsonrpc.core.RemoteInvoker;
import net.apexes.wsonrpc.core.WsonrpcConfig;
import net.apexes.wsonrpc.core.WsonrpcConfigBuilder;
import net.apexes.wsonrpc.json.JsonImplementor;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public final class Wsonrpc {
    private Wsonrpc() {}
    
    /**
     * 
     * @return
     */
    public static WsonrpcClientConfigBuilder config() {
        return new WsonrpcClientConfigBuilder();
    }

    /**
     * 
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static WsonrpcClient client(String url) throws URISyntaxException {
        return config().client(url);
    }

    /**
     * 
     * @param client
     * @return
     */
    public static RemoteInvoker invoker(WsonrpcClient client) {
        return RemoteInvoker.create(client);
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    public static class WsonrpcClientConfigBuilder {
        
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
         * @param executor
         * @return
         */
        public WsonrpcClientConfigBuilder executor(Executor executor) {
            builder.executor(executor);
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
            return new WsonrpcClientImpl(
                    new WsonrpcClientConfigImpl(builder.build(), new URI(url), connector, connectTimeout));
        }

        /**
         * 
         * @param client
         * @return
         */
        public RemoteInvoker invoker(WsonrpcClient client) {
            return RemoteInvoker.create(client);
        }
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
        public Executor getExecutor() {
            return config.getExecutor();
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

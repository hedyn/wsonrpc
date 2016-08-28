/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import java.net.URI;

import net.apexes.wsonrpc.client.support.SimpleWebsocketConnector;
import net.apexes.wsonrpc.core.BinaryWrapper;
import net.apexes.wsonrpc.json.JsonImplementor;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcClientBuilder {

    private final URI uri;
    private final JsonImplementor jsonImpl;
    private final BinaryWrapper binaryWrapper;
    private WebsocketConnector connector;
    private int connectTimeout;

    WsonrpcClientBuilder(URI uri, JsonImplementor jsonImpl, BinaryWrapper binaryWrapper) {
        this.uri = uri;
        this.jsonImpl = jsonImpl;
        this.binaryWrapper = binaryWrapper;
    }

    public WsonrpcClient build() {
        if (connector == null) {
            connector = new SimpleWebsocketConnector();
        }
        return new WsonrpcClientImpl(
                new WsonrpcClientConfigImpl(jsonImpl, binaryWrapper, uri, connector, connectTimeout));
    }

    public WsonrpcClientBuilder connector(WebsocketConnector connector) {
        this.connector = connector;
        return this;
    }

    public WsonrpcClientBuilder connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WsonrpcClientConfigImpl implements WsonrpcClientConfig {

        private JsonImplementor jsonImpl;
        private BinaryWrapper binaryWrapper;
        private URI uri;
        private WebsocketConnector connector;
        private int connectTimeout;

        private WsonrpcClientConfigImpl(JsonImplementor jsonImpl, BinaryWrapper binaryWrapper, URI uri,
                WebsocketConnector connector, int connectTimeout) {
            this.jsonImpl = jsonImpl;
            this.binaryWrapper = binaryWrapper;
            this.uri = uri;
            this.connector = connector;
            this.connectTimeout = connectTimeout;
        }

        @Override
        public JsonImplementor getJsonImplementor() {
            return jsonImpl;
        }

        @Override
        public BinaryWrapper getBinaryWrapper() {
            return binaryWrapper;
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

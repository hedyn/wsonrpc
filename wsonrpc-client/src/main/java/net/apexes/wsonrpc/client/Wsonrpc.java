/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import java.net.URI;
import java.net.URISyntaxException;

import net.apexes.wsonrpc.core.BinaryWrapper;
import net.apexes.wsonrpc.core.RemoteInvoker;
import net.apexes.wsonrpc.json.JsonImplementor;
import net.apexes.wsonrpc.json.support.GsonImplementor;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public final class Wsonrpc {
    private Wsonrpc() {}

    /**
     * 
     * @param jsonImpl
     * @return
     */
    public static Config json(JsonImplementor jsonImpl) {
        return new Config(jsonImpl, null);
    }

    /**
     * 
     * @return
     */
    public static Config binaryWrapper(BinaryWrapper binaryWrapper) {
        return new Config(new GsonImplementor(), binaryWrapper);
    }

    /**
     * 
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static WsonrpcClientBuilder client(String url) throws URISyntaxException {
        return new WsonrpcClientBuilder(new URI(url), new GsonImplementor(), null);
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
     * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
     *
     */
    public static class Config {

        private JsonImplementor jsonImpl;
        private BinaryWrapper binaryWrapper;

        /**
         * 
         * @param jsonImpl
         * @param binaryWrapper
         */
        private Config(JsonImplementor jsonImpl, BinaryWrapper binaryWrapper) {
            this.jsonImpl = jsonImpl;
            this.binaryWrapper = binaryWrapper;
        }

        /**
         * 
         * @param binaryWrapper
         * @return
         */
        public Config binaryWrapper(BinaryWrapper binaryWrapper) {
            this.binaryWrapper = binaryWrapper;
            return this;
        }

        /**
         * 
         * @param url
         * @return
         * @throws URISyntaxException
         */
        public WsonrpcClientBuilder client(String url) throws URISyntaxException {
            return new WsonrpcClientBuilder(new URI(url), jsonImpl, binaryWrapper);
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
}

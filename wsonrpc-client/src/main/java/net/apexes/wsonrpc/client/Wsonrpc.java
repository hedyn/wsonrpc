/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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
    private Wsonrpc() {
    }

    /**
     * 
     * @return
     */
    public static Config config(BinaryWrapper binaryWrapper) {
        return new Config(new GsonImplementor(), binaryWrapper);
    }

    /**
     * 
     * @param jsonSupport
     * @return
     */
    public static Config config(JsonImplementor jsonSupport) {
        return new Config(jsonSupport, null);
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
     * @param url
     * @return
     * @throws Exception
     */
    public static RemoteInvoker jsonrpc(String url) throws Exception {
        return jsonrpc(url, 0);
    }

    /**
     * 
     * @param url
     * @param connectTimeout
     * @return
     * @throws MalformedURLException
     * @throws Exception
     */
    public static RemoteInvoker jsonrpc(String url, int connectTimeout) throws MalformedURLException {
        JsonRpcHttpRemote remote = new JsonRpcHttpRemote(new URL(url), new GsonImplementor(), null);
        remote.setConnectTimeout(connectTimeout);
        return RemoteInvoker.create(remote);
    }

    /**
     * 
     * @param client
     * @return
     */
    public static RemoteInvoker wsonrpc(WsonrpcClient client) {
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
         * @throws Exception
         */
        public RemoteInvoker jsonrpc(String url) throws Exception {
            return jsonrpc(url, 0);
        }

        /**
         * 
         * @param url
         * @param connectTimeout
         * @return
         * @throws MalformedURLException
         * @throws Exception
         */
        public RemoteInvoker jsonrpc(String url, int connectTimeout) throws MalformedURLException {
            JsonRpcHttpRemote remote = new JsonRpcHttpRemote(new URL(url), jsonImpl, binaryWrapper);
            remote.setConnectTimeout(connectTimeout);
            return RemoteInvoker.create(remote);
        }

        /**
         * 
         * @param client
         * @return
         */
        public RemoteInvoker wsonrpc(WsonrpcClient client) {
            return RemoteInvoker.create(client);
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

    }
}

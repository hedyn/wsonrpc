/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import net.apexes.wsonrpc.json.JsonImplementor;
import net.apexes.wsonrpc.json.support.GsonImplementor;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 */
public final class WsonrpcConfigBuilder {

    public static WsonrpcConfig defaultConfig() {
        return create().build();
    }

    public static WsonrpcConfigBuilder create() {
        return new WsonrpcConfigBuilder();
    }

    private JsonImplementor jsonImpl;
    private BinaryWrapper binaryWrapper;
    private WsonrpcExecutor executor;
    private WsonrpcErrorProcessor errorProcessor;

    private WsonrpcConfigBuilder() {
    }

    public WsonrpcConfig build() {
        if (jsonImpl == null) {
            jsonImpl = new GsonImplementor();
        }
        return new WsonrpcConfigImpl(jsonImpl, binaryWrapper, executor, errorProcessor);
    }

    /**
     * @param jsonImpl
     * @return
     */
    public WsonrpcConfigBuilder json(JsonImplementor jsonImpl) {
        this.jsonImpl = jsonImpl;
        return this;
    }

    /**
     * @param binaryWrapper
     * @return
     */
    public WsonrpcConfigBuilder binaryWrapper(BinaryWrapper binaryWrapper) {
        this.binaryWrapper = binaryWrapper;
        return this;
    }

    /**
     * @param executor
     * @return
     */
    public WsonrpcConfigBuilder executor(WsonrpcExecutor executor) {
        this.executor = executor;
        return this;
    }

    /**
     * @param errorProcessor
     * @return
     */
    public WsonrpcConfigBuilder errorProcessor(WsonrpcErrorProcessor errorProcessor) {
        this.errorProcessor = errorProcessor;
        return this;
    }

    /**
     *
     */
    static class WsonrpcConfigImpl implements WsonrpcConfig {

        private final JsonImplementor jsonImpl;
        private final BinaryWrapper binaryWrapper;
        private final WsonrpcExecutor executor;
        private final WsonrpcErrorProcessor errorProcessor;

        private WsonrpcConfigImpl(JsonImplementor jsonImpl,
                                  BinaryWrapper binaryWrapper,
                                  WsonrpcExecutor executor,
                                  WsonrpcErrorProcessor errorProcessor) {
            this.jsonImpl = jsonImpl;
            this.binaryWrapper = binaryWrapper;
            this.executor = executor;
            this.errorProcessor = errorProcessor;
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
        public WsonrpcExecutor getWsonrpcExecutor() {
            return executor;
        }

        @Override
        public WsonrpcErrorProcessor getErrorProcessor() {
            return errorProcessor;
        }
    }

}

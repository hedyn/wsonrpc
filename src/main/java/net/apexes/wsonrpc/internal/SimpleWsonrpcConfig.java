package net.apexes.wsonrpc.internal;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

import net.apexes.wsonrpc.BinaryWrapper;
import net.apexes.wsonrpc.WsonrpcConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class SimpleWsonrpcConfig implements WsonrpcConfig {

    private final ExecutorService execService;
    private final ObjectMapper mapper;
    private final long timeout;

    public SimpleWsonrpcConfig(ExecutorService execService) {
        this(execService, new ObjectMapper());
    }

    public SimpleWsonrpcConfig(ExecutorService execService, long timeout) {
        this(execService, new ObjectMapper(), timeout);
    }

    public SimpleWsonrpcConfig(ExecutorService execService, ObjectMapper mapper) {
        this(execService, mapper, 0);
    }

    public SimpleWsonrpcConfig(ExecutorService execService, ObjectMapper mapper, long timeout) {
        this.execService = execService;
        this.mapper = mapper;
        this.timeout = timeout;
    }

    @Override
    public ExecutorService getExecutorService() {
        return execService;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return mapper;
    }

    @Override
    public BinaryWrapper getBinaryWrapper() {
        return new BinaryWrapper() {

            @Override
            public InputStream wrap(InputStream ips) throws Exception {
                return ips;
            }

            @Override
            public OutputStream wrap(OutputStream ops) throws Exception {
                return ops;
            }
        };
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

}

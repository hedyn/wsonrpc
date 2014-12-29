/**
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.websocket.Session;

import net.apexes.wsonrpc.BinaryWrapper;
import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.RpcHandler;
import net.apexes.wsonrpc.RpcHandler.RpcMessage;
import net.apexes.wsonrpc.WsonrpcConfig;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class WsonrpcDispatcher implements Caller {

    private final ExecutorService execService;
    private final BinaryWrapper binaryProcessor;
    private final RpcHandler rpcHandler;
    private final long timeout;

    private ExceptionProcessor exceptionProcessor;

    public WsonrpcDispatcher(WsonrpcConfig config) {
        this.execService = config.getExecutorService();
        this.binaryProcessor = config.getBinaryWrapper();
        this.timeout = config.getTimeout();
        this.rpcHandler = config.getRpcHandler();
    }

    public void setExceptionProcessor(ExceptionProcessor processor) {
        this.exceptionProcessor = processor;
    }

    public ExceptionProcessor getExceptionProcessor() {
        return exceptionProcessor;
    }

    public void addService(String name, Object service) {
        rpcHandler.addService(name, service);
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void notify(Session session, String serviceName, String methodName, Object argument)
            throws Exception {
        invoke(session, serviceName, methodName, argument, null);
    }

    @Override
    public Future<Object> request(Session session, String serviceName, String methodName, Object argument,
            Type returnType) throws Exception {
        String id = UUID.randomUUID().toString();
        WosonrpcFuture<Object> future = new WosonrpcFuture<>(id, returnType);
        WsonrpcContext.Futures.put(future);
        try {
            invoke(session, serviceName, methodName, argument, id);
            return future;
        } catch (Exception ex) {
            WsonrpcContext.Futures.out(id);
            throw ex;
        }
    }

    private void invoke(Session session, String serviceName, String methodName, Object argument, String id)
            throws Exception {
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        rpcHandler.call(id, serviceName + "." + methodName, argument, binaryProcessor.wrap(ops));
        session.getBasicRemote().sendBinary(ByteBuffer.wrap(ops.toByteArray()));
    }

    public void handleMessage(Session session, ByteBuffer buffer) throws Exception {
        InputStream ips = binaryProcessor.wrap(new ByteArrayInputStream(buffer.array()));
        RpcMessage rpcMessage = rpcHandler.readRpcMessage(ips);
        if (rpcMessage.isRequest()) {
            doHandleRequest(session, rpcMessage.getValue());
        } else if (rpcMessage.getId() != null) {
            WosonrpcFuture<Object> future = WsonrpcContext.Futures.out(rpcMessage.getId());
            if (future != null) {
                Type returnType = future.returnType;
                CallbackImpl callback = new CallbackImpl(future);
                try {
                    rpcHandler.handleResult(rpcMessage.getValue(), returnType, callback);
                } finally {
                    callback.destroy();
                }
            }
        }
    }

    private void doHandleRequest(final Session session, final Object value) {
        execService.execute(new Runnable() {

            @Override
            public void run() {
                WsonrpcContext.Sessions.begin(session);
                try {
                    ByteArrayOutputStream ops = new ByteArrayOutputStream();
                    rpcHandler.handleCall(value, binaryProcessor.wrap(ops));
                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(ops.toByteArray()));
                } catch (Exception ex) {
                    if (exceptionProcessor != null) {
                        exceptionProcessor.onError(ex, value);
                    }
                } finally {
                    WsonrpcContext.Sessions.end();
                }
            }

        });
    }

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class CallbackImpl implements RpcHandler.Callback {

        private WosonrpcFuture<Object> future;

        private CallbackImpl(WosonrpcFuture<Object> future) {
            this.future = future;
        }

        @Override
        public void result(Object value) {
            if (future != null) {
                future.set(value);
            }
        }

        @Override
        public void error(Throwable throwable) {
            if (future != null) {
                future.setException(throwable);
            }
        }

        void destroy() {
            future = null;
        }

    }
}

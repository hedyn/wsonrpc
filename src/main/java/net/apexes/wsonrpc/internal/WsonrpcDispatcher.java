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
import net.apexes.wsonrpc.WsonrpcConfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.googlecode.jsonrpc4j.DefaultExceptionResolver;
import com.googlecode.jsonrpc4j.ExceptionResolver;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.googlecode.jsonrpc4j.JsonRpcMultiServer;
import com.googlecode.jsonrpc4j.NoCloseInputStream;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
class WsonrpcDispatcher implements Caller {

    private final ExecutorService execService;
    private final ObjectMapper mapper;
    private final BinaryWrapper binaryProcessor;
    private final long timeout;
    private final JsonRpcClient jsonRpcClient;
    private final JsonRpcMultiServer jsonRpcServer;

    private ExceptionResolver exceptionResolver = DefaultExceptionResolver.INSTANCE;
    private ExceptionProcessor exceptionProcessor;

    public WsonrpcDispatcher(WsonrpcConfig config) {
        this.execService = config.getExecutorService();
        this.mapper = config.getObjectMapper();
        this.binaryProcessor = config.getBinaryWrapper();
        this.timeout = config.getTimeout();
        jsonRpcClient = new JsonRpcClient(mapper);
        jsonRpcServer = new JsonRpcMultiServer(mapper);
    }

    public void setExceptionResolver(ExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    public void setExceptionProcessor(ExceptionProcessor processor) {
        this.exceptionProcessor = processor;
    }

    public ExceptionProcessor getExceptionProcessor() {
        return exceptionProcessor;
    }

    public void addService(String name, Object handler) {
        jsonRpcServer.addService(name, handler);
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void notify(Session session, String serviceName, String methodName, Object argument)
            throws Exception {
        String id = UUID.randomUUID().toString().replace("-", "");
        invoke(session, serviceName, methodName, argument, id);
    }

    @Override
    public Future<Object> request(Session session, String serviceName, String methodName, Object argument,
            Type returnType) throws Exception {
        String id = UUID.randomUUID().toString().replace("-", "");
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
        jsonRpcClient.invoke(serviceName + "." + methodName, argument, binaryProcessor.wrap(ops), id);
        session.getBasicRemote().sendBinary(ByteBuffer.wrap(ops.toByteArray()));
    }

    public void handle(Session session, ByteBuffer buffer) throws Exception {
        InputStream ips = binaryProcessor.wrap(new ByteArrayInputStream(buffer.array()));
        JsonNode data = mapper.readTree(new NoCloseInputStream(ips));

        // bail on invalid response
        if (!data.isObject()) {
            throw new JsonRpcClientException(0, "Invalid WSON-RPC data", data);
        }
        ObjectNode jsonObject = ObjectNode.class.cast(data);

        if (jsonObject.has("method")) {
            response(session, jsonObject);
        } else {
            accept(jsonObject);
        }
    }

    private void accept(ObjectNode jsonObject) throws Exception {
        JsonNode idNode = jsonObject.get("id");
        if (idNode == null || !idNode.isTextual()) {
            return;
        }
        String id = idNode.textValue();
        WosonrpcFuture<Object> future = WsonrpcContext.Futures.out(id);
        if (future == null) {
            return;
        }

        // detect errors
        JsonNode exceptionNode = jsonObject.get("error");
        if (exceptionNode != null && !exceptionNode.isNull()) {
            // resolve and throw the exception
            Throwable throwable;
            if (exceptionResolver == null) {
                throwable = DefaultExceptionResolver.INSTANCE.resolveException(jsonObject);
            } else {
                throwable = exceptionResolver.resolveException(jsonObject);
            }
            future.setException(throwable);
        }

        // convert it to a return object
        JsonNode resultNode = jsonObject.get("result");
        if (resultNode != null && !resultNode.isNull()) {
            Type returnType = future.returnType;
            if (returnType == null) {
                return;
            }
            JsonParser returnJsonParser = mapper.treeAsTokens(resultNode);
            JavaType returnJavaType = TypeFactory.defaultInstance().constructType(returnType);
            Object value = mapper.readValue(returnJsonParser, returnJavaType);
            future.set(value);
        } else {
            Throwable throwable;
            if (exceptionResolver == null) {
                throwable = DefaultExceptionResolver.INSTANCE.resolveException(jsonObject);
            } else {
                throwable = exceptionResolver.resolveException(jsonObject);
            }
            future.setException(throwable);
        }
    }

    private void response(final Session session, final ObjectNode jsonObject) {
        execService.execute(new Runnable() {

            @Override
            public void run() {
                WsonrpcContext.Sessions.begin(session);
                try {
                    ByteArrayOutputStream ops = new ByteArrayOutputStream();
                    jsonRpcServer.handleNode(jsonObject, binaryProcessor.wrap(ops));
                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(ops.toByteArray()));
                } catch (Exception ex) {
                    if (exceptionProcessor != null) {
                        exceptionProcessor.onError(ex, jsonObject);
                    }
                } finally {
                    WsonrpcContext.Sessions.end();
                }
            }

        });
    }

}

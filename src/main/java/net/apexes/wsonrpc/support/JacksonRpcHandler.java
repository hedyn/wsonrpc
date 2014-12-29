/**
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import net.apexes.wsonrpc.RpcHandler;

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
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JacksonRpcHandler implements RpcHandler {

    private final ObjectMapper objectMapper;
    private final JsonRpcClient jsonRpcClient;
    private final JsonRpcMultiServer jsonRpcServer;

    private ExceptionResolver exceptionResolver = DefaultExceptionResolver.INSTANCE;

    public JacksonRpcHandler() {
        this(new ObjectMapper());
    }

    public JacksonRpcHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        jsonRpcClient = new JsonRpcClient(objectMapper);
        jsonRpcServer = new JsonRpcMultiServer(objectMapper);
    }

    public void setExceptionResolver(ExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    public void addService(String name, Object service) {
        jsonRpcServer.addService(name, service);
    }

    @Override
    public void invoke(String id, String methodName, Object argument, OutputStream ops) throws IOException {
        jsonRpcClient.invoke(methodName, argument, ops, id);
    }

    @Override
    public RpcMessage readRpcMessage(InputStream ips) throws Exception {
        JsonNode data = objectMapper.readTree(new NoCloseInputStream(ips));
        if (!data.isObject()) {
            throw new JsonRpcClientException(0, "Invalid WSON-RPC data", data);
        }
        ObjectNode jsonObject = ObjectNode.class.cast(data);
        JsonNode idNode = jsonObject.get("id");
        if (idNode == null || !idNode.isTextual()) {
            return null;
        }
        String id = idNode.textValue();
        if (jsonObject.has("method")) {
            return RpcMessage.createInvokeMessage(id, jsonObject);
        } else {
            return RpcMessage.createResultMessage(id, jsonObject);
        }
    }

    @Override
    public void handleInvoke(Object value, OutputStream ops) throws IOException {
        jsonRpcServer.handleNode((ObjectNode) value, ops);
    }

    @Override
    public void handleResult(Object value, Type returnType, Callback callback) throws IOException {
        ObjectNode jsonObject = (ObjectNode) value;
        JsonNode exceptionNode = jsonObject.get("error");
        if (exceptionNode != null && !exceptionNode.isNull()) {
            // resolve and throw the exception
            Throwable throwable;
            if (exceptionResolver == null) {
                throwable = DefaultExceptionResolver.INSTANCE.resolveException(jsonObject);
            } else {
                throwable = exceptionResolver.resolveException(jsonObject);
            }
            callback.error(throwable);
        }

        // convert it to a return object
        JsonNode resultNode = jsonObject.get("result");
        if (resultNode != null && !resultNode.isNull()) {
            JsonParser returnJsonParser = objectMapper.treeAsTokens(resultNode);
            JavaType returnJavaType = TypeFactory.defaultInstance().constructType(returnType);
            Object resultObject = objectMapper.readValue(returnJsonParser, returnJavaType);
            callback.result(resultObject);
        } else {
            Throwable throwable;
            if (exceptionResolver == null) {
                throwable = DefaultExceptionResolver.INSTANCE.resolveException(jsonObject);
            } else {
                throwable = exceptionResolver.resolveException(jsonObject);
            }
            callback.error(throwable);
        }
    }

}

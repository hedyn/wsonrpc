/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core.message;

import net.apexes.wsonrpc.core.JsonException;
import net.apexes.wsonrpc.json.JsonImplementor;
import net.apexes.wsonrpc.json.JsonImplementor.Node;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcResponse extends JsonRpcMessage {
    
    private final Node result;
    private final JsonRpcError error;
    
    public JsonRpcResponse(String id, Node result) {
        super(id);
        if (id == null) {
            throw new NullPointerException("id");
        }
        this.result = result;
        this.error = null;
    }
    
    public JsonRpcResponse(String id, JsonRpcError error) {
        super(id);
        if (error == null) {
            throw new NullPointerException("error");
        }
        this.error = error;
        this.result = null;
    }

    public Node getResult() {
        return result;
    }

    public JsonRpcError getError() {
        return error;
    }

    @Override
    public String toJson(JsonImplementor jsonImpl) throws JsonException {
        Node resp = jsonImpl.createNode();
        resp.put("jsonrpc", "2.0");
        if (id != null) {
            resp.put("id", id);
        }
        if (error != null) {
            Node errorNode = jsonImpl.createNode();
            errorNode.put("code", error.getCode());
            errorNode.put("message", error.getMessage());
            if (error.getData() != null) {
                errorNode.put("data", error.getData());
            }
            resp.put("error", errorNode);
        } else if (result != null) {
            resp.put("result", result);
        }
        return jsonImpl.toJson(resp);
    }

}

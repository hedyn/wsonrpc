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
public class JsonRpcRequest extends JsonRpcMessage {
    
    private final String method;
    private final Node[] params;
    
    public JsonRpcRequest(String id, String method, Node[] params) {
        super(id);
        if (method == null) {
            throw new NullPointerException("method");
        }
        this.method = method;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public Node[] getParams() {
        return params;
    }
    
    public boolean isNotice() {
        return id == null;
    }

    @Override
    public String toJson(JsonImplementor jsonImpl) throws JsonException {
        Node req = jsonImpl.createNode();
        req.put("jsonrpc", "2.0");
        req.put("method", method);
        if (params != null && params.length > 0) {
            req.put("params", params);
        }
        if (id != null) {
            req.put("id", id);
        }
        return jsonImpl.toJson(req);
    }
}

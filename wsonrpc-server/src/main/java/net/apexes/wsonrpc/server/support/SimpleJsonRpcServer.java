/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server.support;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.apexes.wsonrpc.core.JsonRpcControl;
import net.apexes.wsonrpc.core.ServiceRegistry;
import net.apexes.wsonrpc.core.Transport;
import net.apexes.wsonrpc.core.WsonrpcException;
import net.apexes.wsonrpc.json.JsonImplementor;
import net.apexes.wsonrpc.json.support.GsonImplementor;
import net.apexes.wsonrpc.server.support.http.NanoHTTPD;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class SimpleJsonRpcServer extends NanoHTTPD {
    
    private final JsonRpcControl jsonRpcControl;
    
    public SimpleJsonRpcServer(int port) {
        this(port, new GsonImplementor());
    }

    public SimpleJsonRpcServer(int port, JsonImplementor jsonImpl) {
        super(port);
        jsonRpcControl = new JsonRpcControl(jsonImpl);
    }
    
    public ServiceRegistry getServiceRegistry() {
        return jsonRpcControl;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> files = new HashMap<String, String>();
        Method method = session.getMethod();
        if (Method.POST.equals(method)) {
            try {
                session.parseBody(files);
                String json = files.get("postData");
                TransportImpl transport = new TransportImpl();
                jsonRpcControl.receiveRequest(json.getBytes(), transport);
                return newFixedLengthResponse(transport.json);
            } catch (IOException e) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT,
                        "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
            } catch (WsonrpcException e) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT,
                        "SERVER INTERNAL ERROR: WsonrpcException: " + e.getMessage());
            } catch (ResponseException e) {
                return newFixedLengthResponse(e.getStatus(), NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
            }
        }
        return super.serve(session);
    }

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private class TransportImpl implements Transport {

        private String json;

        @Override
        public void sendBinary(byte[] bytes) throws IOException {
            this.json = new String(bytes, "UTF-8");
        }

    };

}

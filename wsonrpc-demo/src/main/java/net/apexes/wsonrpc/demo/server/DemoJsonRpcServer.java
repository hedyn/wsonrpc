/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.util.ServerRunner;
import net.apexes.wsonrpc.core.JsonRpcKernel;
import net.apexes.wsonrpc.core.Transport;
import net.apexes.wsonrpc.demo.api.DemoService;
import net.apexes.wsonrpc.demo.server.service.DemoServiceImpl;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class DemoJsonRpcServer extends NanoHTTPD {

    private static final Logger LOG = LoggerFactory.getLogger(DemoJsonRpcServer.class);

    public static void main(String[] args) {
        LOG.debug("...");
        ServerRunner.run(DemoJsonRpcServer.class);
    }

    private final JsonRpcKernel jsonRpcKernel;

    public DemoJsonRpcServer() {
        super(8080);
//        jsonRpcKernel = new JsonRpcKernel(new net.apexes.wsonrpc.json.support.JacksonImplementor());
        jsonRpcKernel = new JsonRpcKernel(new net.apexes.wsonrpc.json.support.GsonImplementor());
        jsonRpcKernel.register("demo", new DemoServiceImpl(), DemoService.class);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> files = new HashMap<String, String>();
        Method method = session.getMethod();
        if (Method.POST.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException ioe) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT,
                        "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            } catch (ResponseException re) {
                return newFixedLengthResponse(re.getStatus(), NanoHTTPD.MIME_PLAINTEXT, re.getMessage());
            }
            
            String json = session.getParms().keySet().iterator().next();
            try {
                TransportImpl transport = new TransportImpl();
                jsonRpcKernel.receiveRequest(json.getBytes("UTF-8"), transport);
                return newFixedLengthResponse(transport.json);
            } catch (Exception e) {
                e.printStackTrace();
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

/**
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.tyrus.server.Server;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class WsonrpcServerDemo {

    public static void main(String[] args) {
        runServer();
    }

    public static void runServer() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("org.glassfish.tyrus.maxSessionsPerRemoteAddr", 10000);
        Server server = new Server("localhost", 8080, null, properties, WsonrpcService.class);
        try {
            server.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please press a key to stop the server.");
            reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            server.stop();
        }
    }

}

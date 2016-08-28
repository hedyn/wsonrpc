/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.client;

import net.apexes.wsonrpc.client.Wsonrpc;
import net.apexes.wsonrpc.demo.api.DemoHandler;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class DemoJsonRcpClient {
    
    public static void main(String[] args) throws Exception {
        DemoHandler demoHandler = Wsonrpc.jsonrpc("http://127.0.0.1:8080")
                .handleName("demo")
                .get(DemoHandler.class);
        System.out.println(demoHandler.echo("Hello wsonrpc!"));
        System.out.println(demoHandler.login("admin", "admin"));
        System.out.println(demoHandler.getRoleList());
        System.out.println(demoHandler.getDept("1"));
        System.out.println(demoHandler.getDeptList());
    }

}

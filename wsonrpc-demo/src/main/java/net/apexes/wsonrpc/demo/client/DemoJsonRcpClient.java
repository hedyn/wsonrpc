/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.client;

import java.util.Arrays;

import net.apexes.wsonrpc.client.Jsonrpc;
import net.apexes.wsonrpc.core.RemoteInvoker;
import net.apexes.wsonrpc.demo.api.DemoHandler;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class DemoJsonRcpClient {
    
    public static void main(String[] args) throws Exception {
        RemoteInvoker invoker = Jsonrpc.url("http://127.0.0.1:8080")
                .json(new net.apexes.wsonrpc.json.support.JacksonImplementor())
                .invoker();
        DemoHandler demoHandler = invoker.handleName("demo").get(DemoHandler.class);
        System.out.println(demoHandler.echo("Hello wsonrpc!"));
        System.out.println(demoHandler.login("admin", "admin"));
        System.out.println(demoHandler.getRoleList());
        System.out.println(demoHandler.getDept("1"));
        System.out.println(demoHandler.getDeptList());
        System.out.println(demoHandler.listUser(Arrays.asList("admin", "1001")));
    }

}

/**
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.client;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.client.WsonrpcClient;
import net.apexes.wsonrpc.demo.api.LoginService;
import net.apexes.wsonrpc.demo.api.User;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class WsonrpcClientDemo {

    private static CountDownLatch downLatch = new CountDownLatch(3);

    public static void main(String[] args) throws Exception {
        ExecutorService execService = Executors.newCachedThreadPool();
        WsonrpcConfig config = WsonrpcConfig.Builder.create().build(execService);
        URI uri = new URI("ws://127.0.0.1:8080/wsonrpc");
        WsonrpcClient client = WsonrpcClient.Builder.create(uri, config);
        client.addService("callClientService", new CallClientServiceImpl());
        client.setExceptionProcessor(new ExceptionProcessor() {

            @Override
            public void onError(Throwable throwable, Object... params) {
                if (params != null) {
                    System.err.println(Arrays.toString(params));
                }
                throwable.printStackTrace();
            }
        });
        client.connect();

        LoginService srv = WsonrpcRemote.Executor.createProxy(client, LoginService.class, "loginService");
        User user = srv.login("admin", "admin");
        System.out.println("login: " + user);

        Future<String> future = client.asyncInvoke("loginService", "login1", new Object[] { "async", "async" },
                String.class);
        System.out.println("async login: " + future.get(3, TimeUnit.SECONDS));

        Future<String> future4 = null;
        try {
            future4 = client.asyncInvoke("loginService", "login4", new Object[] { "async", "async" }, String.class);
            System.out.println("async login: " + future4.get(3, TimeUnit.SECONDS));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            future4 = null;
        }

        testLogin1(client);
        testLogin2(client);
        testLogin3(client);

        System.out.println("sleep....");
        downLatch.await(20, TimeUnit.SECONDS);
        System.out.println("sleep end.");

        client.close();
        execService.shutdownNow();
        System.out.println("over.");
    }

    private static void testLogin1(final WsonrpcClient client) throws Exception {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    LoginService srv = WsonrpcRemote.Executor.createProxy(client, LoginService.class, "loginService");
                    String user = srv.login1("login1_" + i, "123456");
                    System.out.println("login1 result: " + user);
                }
                System.out.println("login1 OVER!");
                downLatch.countDown();
            }
        };
        new Thread(runnable, "#1").start();
    }

    private static void testLogin2(final WsonrpcClient client) throws Exception {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    LoginService srv = WsonrpcRemote.Executor.createProxy(client, LoginService.class, "loginService");
                    String user = srv.login2("login2_" + i, "123456");
                    System.out.println("login2 result: " + user);
                }
                System.out.println("login2 OVER!");
                downLatch.countDown();
            }
        };
        new Thread(runnable, "#2").start();
    }

    private static void testLogin3(final WsonrpcClient client) throws Exception {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    LoginService srv = WsonrpcRemote.Executor.createProxy(client, LoginService.class, "loginService");
                    String user = srv.login3("login3_" + i, "123456");
                    System.out.println("login3 result: " + user);
                }
                System.out.println("login3 OVER!");
                downLatch.countDown();
            }
        };
        new Thread(runnable, "#3").start();
    }

}

package net.apexes.wsonrpc.demo.client;

import java.net.URI;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcRemote;
import net.apexes.wsonrpc.client.ClientStatusListener;
import net.apexes.wsonrpc.client.WsonrpcClient;
import net.apexes.wsonrpc.client.support.SimpleWebsocketConnector;
import net.apexes.wsonrpc.demo.api.LoginService;
import net.apexes.wsonrpc.demo.api.User;
import net.apexes.wsonrpc.support.GsonJsonHandler;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
@SuppressWarnings("unused")
public class WsonrpcClientDemo {

    static final int CLIENT_COUNT = 1000;
    static final int THREAD_COUNT = 10;
    static final int LOOP_COUNT = 100;
    private static CountDownLatch clientDownLatch;
    private static ExecutorService execService = Executors.newCachedThreadPool();
    
    public static void main(String[] args) throws Exception {
        clientDownLatch = new CountDownLatch(CLIENT_COUNT);
        for (int i = 0; i < CLIENT_COUNT; i++) {
            execService.execute(new ClientTask(i));
        }
        
        System.out.println("sleep....");
        clientDownLatch.await(120, TimeUnit.SECONDS);
        System.out.println("sleep end.==" + clientDownLatch.getCount());
        
        Thread.sleep(1000);
        
        execService.shutdownNow();
        System.out.println("Over!");
    }
    
    private static void testClient(final int clientIndex) throws Exception {
        WsonrpcConfig config = WsonrpcConfig.Builder.create().build(execService);
        URI uri = new URI("ws://127.0.0.1:8080/wsonrpc");
        WsonrpcClient client = WsonrpcClient.Builder.create(uri, config);
        
        // 供Server端调用的接口
        client.addService("callClientService", new CallClientServiceImpl());
        
        client.setStatusListener(new ClientStatusListener() {

            @Override
            public void onOpen(WsonrpcClient client) {
                try {
                    testInvoke(client, clientIndex);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    clientDownLatch.countDown();
                }
            }

            @Override
            public void onClose(WsonrpcClient client) {}
        });
        
        client.connect();
    }
    
    static void testInvoke(WsonrpcClient client, int clientIndex) throws Exception {
        // 异步调用
        Future<User> future = client.asyncInvoke("loginService", "login",
                new Object[] { "async", "async" }, User.class);
        System.out.println("@" + clientIndex + ": async login: " + future.get(10, TimeUnit.SECONDS));
                
        // 同步调用
        LoginService srv = WsonrpcRemote.Executor.createProxy(client, LoginService.class, "loginService");
        User user = srv.login("admin", "admin");
        System.out.println("@" + clientIndex + ": login(String,String): " + user);
        
        user = srv.login(user);
        System.out.println("@" + clientIndex + ": login(User): " + user);
        
//        CountDownLatch threadDownLatch = new CountDownLatch(THREAD_COUNT);
//        for (int i = 0; i < THREAD_COUNT; i++) {
//            testLogin1(clientIndex, client, threadDownLatch);
//            testLogin2(clientIndex, client, threadDownLatch);
//            testLogin3(clientIndex, client, threadDownLatch);
//        }
//        System.out.println("@" + clientIndex + "sleep....");
//        threadDownLatch.await(1200, TimeUnit.SECONDS);
//        System.out.println("@" + clientIndex + "sleep end.==" + threadDownLatch.getCount());
        
//        Future<String> future4 = null;
//        try {
//            future4 = client.asyncInvoke("loginService", "login4", new Object[] { "async", "async" }, String.class);
//            System.out.println("async login: " + future4.get(3, TimeUnit.SECONDS));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            future4 = null;
//        }
        
        client.close();
        System.out.println("@" + clientIndex + ": over.");
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class ClientTask implements Runnable {
        
        private final int clientIndex;
        
        public ClientTask(int clientIndex) {
            this.clientIndex = clientIndex;
        }
        
        @Override
        public void run() {
            try {
                testClient(clientIndex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static final int RANDOM = 500;
        
    private static AtomicInteger threadCounter = new AtomicInteger(0);
    
    static void testLogin1(final int clientIndex, final WsonrpcClient client,
            final CountDownLatch latch) throws Exception {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                System.out.println("@" + clientIndex + "#" + threadCounter.incrementAndGet());
                Random random = new Random();
                for (int i = 0; i < LOOP_COUNT; i++) {
                    try {
                        int r = random.nextInt(RANDOM);
                        Thread.sleep(r);
                    } catch (InterruptedException e) {
                    }
                    LoginService srv = WsonrpcRemote.Executor.createProxy(client, LoginService.class, "loginService");
                    String user = srv.login1("login1_" + i, "123456");
//                    System.out.println("login1 result: " + user);
                }
                System.out.println("@" + clientIndex + ": login1 OVER!");
                latch.countDown();
            }
        };
        execService.execute(runnable);
    }

    static void testLogin2(final int clientIndex, final WsonrpcClient client,
            final CountDownLatch latch) throws Exception {
        Runnable runnable = new Runnable() {
            
            @Override
            public void run() {
                System.out.println("@" + clientIndex + "#" + threadCounter.incrementAndGet());
                Random random = new Random();
                for (int i = 0; i < LOOP_COUNT; i++) {
                    try {
                        int r = random.nextInt(RANDOM);
                        Thread.sleep(r);
                    } catch (InterruptedException e) {
                    }
                    LoginService srv = WsonrpcRemote.Executor.createProxy(client, LoginService.class, "loginService");
                    String user = srv.login2("login2_" + i, "123456");
//                    System.out.println("login2 result: " + user);
                }
                System.out.println("@" + clientIndex + ": login2 OVER!");
                latch.countDown();
            }
        };
        execService.execute(runnable);
    }

    static void testLogin3(final int clientIndex, final WsonrpcClient client,
            final CountDownLatch latch) throws Exception {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                System.out.println("@" + clientIndex + "#" + threadCounter.incrementAndGet());
                Random random = new Random();
                for (int i = 0; i < LOOP_COUNT; i++) {
                    try {
                        int r = random.nextInt(RANDOM);
                        Thread.sleep(r);
                    } catch (InterruptedException e) {
                    }
                    LoginService srv = WsonrpcRemote.Executor.createProxy(client, LoginService.class, "loginService");
                    String user = srv.login3("login3_" + i, "123456");
//                    System.out.println("login3 result: " + user);
                }
                System.out.println("@" + clientIndex + ": login3 OVER!");
                latch.countDown();
            }
        };
        execService.execute(runnable);
    }

}

package net.apexes.wsonrpc.demo.client;

import java.net.URI;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.apexes.jsonrpc.GsonJsonContext;
import net.apexes.jsonrpc.JsonRpcLogger;
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
@SuppressWarnings("unused")
public class WsonrpcClientDemo {

    static final int CLIENT_COUNT = 1;
    static final int THREAD_COUNT = 1;//实际为 THREAD_COUNT * 3
    static final int LOOP_COUNT = 1;
    private static CountDownLatch clientDownLatch;
    private static ExecutorService execService = Executors.newCachedThreadPool();
    
    public static void main(String[] args) throws Exception {
        clientDownLatch = new CountDownLatch(CLIENT_COUNT);
        for (int i = 0; i < CLIENT_COUNT; i++) {
            execService.execute(new ClientTask(i));
        }
        
        System.out.println("sleep....");
        clientDownLatch.await(150, TimeUnit.SECONDS);
        System.out.println("sleep end.==" + clientDownLatch.getCount());
        
        Thread.sleep(1000);
        
        execService.shutdownNow();
        System.out.println("Over!");
    }
    
    private static void testClient(final int clientIndex) throws Exception {
        GsonJsonContext jsonContext = new GsonJsonContext();
        jsonContext.setLogger(new JsonRpcLogger() {

            @Override
            public void onRead(String json) {
                System.err.println("onRead: " + json);
            }

            @Override
            public void onWrite(String json) {
                System.err.println("onWrite: " + json);
            }
        });
        WsonrpcConfig config = WsonrpcConfig.Builder.create().jsonContext(jsonContext).build(execService);
        URI uri = new URI("ws://127.0.0.1:8080/wsonrpc/" + clientIndex);
        WsonrpcClient client = WsonrpcClient.Builder.create(uri)
                //.connector(new net.apexes.wsonrpc.client.support.JavaWebsocketConnector())
                //.connector(new net.apexes.wsonrpc.client.support.TyrusWebsocketConnector())
                .build(config);
        
        // 供Server端调用的接口
        client.getServiceRegistry().register(new CallClientServiceImpl());
        client.setExceptionProcessor(new ExceptionProcessor() {

            @Override
            public void onError(Throwable error, Object... params) {
                error.printStackTrace();
            }
        });
        
        client.connect();
        
        try {
            testInvoke(client, clientIndex);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            client.close();
            clientDownLatch.countDown();
        }
    }
    
    static void testInvoke(WsonrpcClient client, int clientIndex) throws Exception {
        // 异步调用
        Future<User> future = client.asyncInvoke(LoginService.class.getName(), "login",
                new Object[] { "async", "async" }, User.class);
        System.out.println("@" + clientIndex + ": async login: " + future.get(10, TimeUnit.SECONDS));
        
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("user1.password");
        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("user2.password");
        // 同步调用
        LoginService srv = getLoginService(client);
        User user = srv.login(user1, user2);
        System.out.println("@" + clientIndex + ": login(User,User): " + user);
        
        user = srv.login(user);
//        System.out.println("@" + clientIndex + ": login(User): " + user);
        
        CountDownLatch threadDownLatch = new CountDownLatch(THREAD_COUNT * 3);
        for (int i = 0; i < THREAD_COUNT; i++) {
            testLogin1(clientIndex, client, threadDownLatch);
            testLogin2(clientIndex, client, threadDownLatch);
            testLogin3(clientIndex, client, threadDownLatch);
        }
//        System.out.println("@" + clientIndex + "sleep....");
        threadDownLatch.await(120, TimeUnit.SECONDS);
        System.out.println("@" + clientIndex + ": sleep end.==" + threadDownLatch.getCount());
        
//        Future<String> future4 = null;
//        try {
//            future4 = client.asyncInvoke("loginService", "login4", new Object[] { "async", "async" }, String.class);
//            System.out.println("async login: " + future4.get(3, TimeUnit.SECONDS));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            future4 = null;
//        }
        
//        System.out.println("@" + clientIndex + ": over.");
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
    
    static LoginService getLoginService(WsonrpcClient client) {
        return WsonrpcRemote.Executor.create(client).getService(LoginService.class);
    }
    
    private static final int RANDOM = 100;
        
    private static AtomicInteger threadCounter = new AtomicInteger(0);
    
    static void testLogin1(final int clientIndex, final WsonrpcClient client,
            final CountDownLatch latch) throws Exception {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                System.out.println("@" + clientIndex + "#" + threadCounter.incrementAndGet() + " start...");
                Random random = new Random();
                for (int i = 0; i < LOOP_COUNT; i++) {
                    try {
                        int r = random.nextInt(RANDOM);
                        Thread.sleep(r);
                    } catch (InterruptedException e) {
                    }
                    LoginService srv = getLoginService(client);
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
                System.out.println("@" + clientIndex + "#" + threadCounter.incrementAndGet() + " start...");
                Random random = new Random();
                for (int i = 0; i < LOOP_COUNT; i++) {
                    try {
                        int r = random.nextInt(RANDOM);
                        Thread.sleep(r);
                    } catch (InterruptedException e) {
                    }
                    LoginService srv = getLoginService(client);
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
                System.out.println("@" + clientIndex + "#" + threadCounter.incrementAndGet() + " start...");
                Random random = new Random();
                for (int i = 0; i < LOOP_COUNT; i++) {
                    try {
                        int r = random.nextInt(RANDOM);
                        Thread.sleep(r);
                    } catch (InterruptedException e) {
                    }
                    LoginService srv = getLoginService(client);
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

package net.apexes.wsonrpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.concurrent.Future;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcRemote {

    String getSessionId();
    
    boolean isOpen();

    void close() throws Exception;
    
    /**
     * 返回超时时间，0表示永不超时。单位为TimeUnit.MILLISECONDS
     * 
     * @return
     */
    long getTimeout();

    void notify(String serviceName, String methodName, Object argument) throws Exception;

    Object invoke(String serviceName, String methodName, Object argument, Type returnType) throws Exception;

    Object invoke(String serviceName, String methodName, Object argument, Type returnType, long timeout)
            throws Exception;

    <T> T invoke(String serviceName, String methodName, Object argument, Class<T> returnType)
            throws Exception;

    <T> T invoke(String serviceName, String methodName, Object argument, Class<T> returnType, long timeout)
            throws Exception;

    Future<Object> asyncInvoke(String serviceName, String methodName, Object argument, Type returnType)
            throws Exception;

    <T> Future<T> asyncInvoke(String serviceName, String methodName, Object argument, Class<T> returnType)
            throws Exception;

    /**
     * 
     * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
     *
     */
    final class Executor {

        public static Executor create(WsonrpcRemote remote) {
            return new Executor(remote);
        }

        private final WsonrpcRemote remote;
        private String serviceName;
        private ClassLoader classLoader;
        private long timeout;

        private Executor(WsonrpcRemote remote) {
            this.remote = remote;
        }

        public Executor serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Executor classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Executor timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T getService(final Class<T> serviceClass) {
            if (serviceName == null) {
                serviceName = serviceClass.getName();
            }
            if (classLoader == null) {
                classLoader = serviceClass.getClassLoader();
            }
            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getDeclaringClass() == Object.class) {
                        return proxyObjectMethods(method, proxy, args);
                    }
                    Object argument = null;
                    if (args != null) {
                        if (args.length == 1) {
                            argument = args[0];
                        } else {
                            argument = args;
                        }
                    }
                    Type returnType = method.getGenericReturnType();
                    if (returnType == Void.TYPE) {
                        remote.notify(serviceName, method.getName(), argument);
                        return null;
                    }
                    return remote.invoke(serviceName, method.getName(), argument, returnType, timeout);
                }
            };
            return (T) Proxy.newProxyInstance(classLoader, new Class<?>[] { serviceClass }, handler);
        }

        private static Object proxyObjectMethods(Method method, Object proxyObject, Object[] args) {
            String name = method.getName();
            if (name.equals("toString")) {
                return proxyObject.getClass().getName() + "@" + System.identityHashCode(proxyObject);
            }
            if (name.equals("hashCode")) {
                return System.identityHashCode(proxyObject);
            }
            if (name.equals("equals")) {
                return proxyObject == args[0];
            }
            throw new RuntimeException(method.getName() + " is not a member of java.lang.Object");
        }

    }

}

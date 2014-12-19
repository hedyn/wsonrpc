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

    void close() throws Exception;

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
    public static class Executor {

        public static <T> T createProxy(final WsonrpcRemote remote, final Class<T> serviceClass) {
            return createProxy(remote, serviceClass, 0);
        }

        public static <T> T createProxy(final WsonrpcRemote remote, final Class<T> serviceClass,
                final long timeout) {
            return createProxy(remote, serviceClass, serviceClass.getSimpleName(), timeout);
        }

        public static <T> T createProxy(final WsonrpcRemote remote, final Class<T> serviceClass,
                final String serviceName) {
            return createProxy(remote, serviceClass, serviceClass.getClassLoader(), serviceName, 0);
        }

        public static <T> T createProxy(final WsonrpcRemote remote, final Class<T> serviceClass,
                final String serviceName, final long timeout) {
            return createProxy(remote, serviceClass, serviceClass.getClassLoader(), serviceName, timeout);
        }

        public static <T> T createProxy(final WsonrpcRemote remote, final Class<T> serviceClass,
                final ClassLoader classLoader, final String serviceName) {
            return createProxy(remote, serviceClass, classLoader, serviceName, 0);
        }

        @SuppressWarnings("unchecked")
        public static <T> T createProxy(final WsonrpcRemote remote, final Class<T> serviceClass,
                final ClassLoader classLoader, final String serviceName, final long timeout) {
            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getDeclaringClass() == Object.class) {
                        return proxyObjectMethods(method, proxy, args);
                    }
                    Type returnType = method.getGenericReturnType();
                    return remote.invoke(serviceName, method.getName(), args, returnType, timeout);
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

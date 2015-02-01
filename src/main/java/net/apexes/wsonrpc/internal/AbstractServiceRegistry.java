package net.apexes.wsonrpc.internal;

import net.apexes.wsonrpc.ServiceRegistry;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry {

    @Override
    public void register(Object handler) {
        if (handler == null) {
            throw new IllegalArgumentException("The handler must be not null.");
        }
        Class<?>[] interfaces = handler.getClass().getInterfaces();
        if (interfaces != null) {
            for (Class<?> interfaceClass : interfaces) {
                register(interfaceClass.getName(), handler);
            }
        } else {
            throw new IllegalArgumentException("The handler must implements interface.");
        }
    }
}

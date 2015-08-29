/**
 * Copyright (C) 2015, Apexes Network Technology. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.jsonrpc;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class SimpleServiceMethodFinder implements ServiceMethodFinder {
    
    private final Map<String, Object> serviceFinder;
    private final Map<String, Set<Method>> methodCache;
    
    protected SimpleServiceMethodFinder() {
        serviceFinder = new ConcurrentHashMap<String, Object>();
        methodCache = new HashMap<String, Set<Method>>();
    }

    @Override
    public void register(String name, Object service) {
        serviceFinder.put(name, service);
    }

    @Override
    public void register(Object service) {
        if (service == null) {
            throw new IllegalArgumentException("The service must be not null.");
        }
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces != null) {
            for (Class<?> interfaceClass : interfaces) {
                register(interfaceClass.getName(), service);
            }
        } else {
            throw new IllegalArgumentException("The service must implements interface.");
        }
    }

    @Override
    public MethodHolder find(String serviceMethod) {
        int index = serviceMethod.lastIndexOf(".");
        String serviceName = serviceMethod.substring(0, index);
        String methodName = serviceMethod.substring(index + 1);
        Object service = serviceFinder.get(serviceName);
        return new MethodHolder(service, findMethods(service.getClass(), methodName));
    }
    
    /**
     * Finds methods with the given name on the given class.
     * @param clazz the class
     * @param name the method name
     * @return the methods
     */
    private Set<Method> findMethods(Class<?> clazz, String name) {
        String cacheKey = clazz.getName() + "." + name;
        if (methodCache.containsKey(cacheKey)) {
            return methodCache.get(cacheKey);
        }
        Set<Method> methods = new HashSet<Method>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(name)) {
                methods.add(method);
            }
        }
        methods = Collections.unmodifiableSet(methods);
        methodCache.put(cacheKey, methods);
        return methods;
    }

}

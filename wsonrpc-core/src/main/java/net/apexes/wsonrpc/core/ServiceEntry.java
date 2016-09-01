/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 * @param <T>
 */
class ServiceEntry<T> {

    private final T service;
    private final Map<String, Set<Method>> methods;

    public ServiceEntry(T service, Class<?>... classes) {
        if (service == null) {
            throw new NullPointerException("service");
        }

        if (classes.length == 0) {
            throw new IllegalArgumentException("none interface");
        }

        this.service = service;

        Map<String, Set<Method>> map = new HashMap<>();

        Class<?> serviceClass = service.getClass();
        for (Class<?> clazz : classes) {
            if (!clazz.isInterface()) {
                throw new IllegalArgumentException("class should be an interface : " + clazz);
            }

            for (Method m : clazz.getMethods()) {
                String methodName = m.getName();
                Class<?>[] params = m.getParameterTypes();

                Method method;
                try {
                    method = serviceClass.getMethod(methodName, params);
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException(
                            "not implements method : " + methodName + argumentTypesToString(params));
                } catch (SecurityException e) {
                    throw new IllegalArgumentException(
                            "access error : " + methodName + argumentTypesToString(params), e);
                }
                Set<Method> set = map.get(methodName);
                if (set == null) {
                    set = new HashSet<>();
                    map.put(methodName, set);
                }
                set.add(method);
            }
        }

        this.methods = Collections.unmodifiableMap(map);
    }

    public T getService() {
        return service;
    }

    public Set<Method> getMethods(String methodName) {
        return methods.get(methodName);
    }

    private static String argumentTypesToString(Class<?>[] argTypes) {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        if (argTypes != null) {
            for (int i = 0; i < argTypes.length; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                Class<?> c = argTypes[i];
                buf.append((c == null) ? "null" : c.getName());
            }
        }
        buf.append(")");
        return buf.toString();
    }
}

/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.json;

import net.apexes.wsonrpc.core.JsonException;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface JsonImplementor {
    
    /**
     * 将JSON格式文本反序列化为{@link Node}对象
     * 
     * @param json JSON格式文本
     * @return 返回{@link Node}对象
     * @throws Exception Exception
     */
    Node fromJson(String json) throws JsonException;
    
    /**
     * 将{@link Node}对象序列化为JSON格式文本
     * 
     * @param node {@link Node}对象
     * @return 返回序列化后的JSON格式文本
     * @throws Exception Exception
     */
    String toJson(Node node) throws JsonException;
    
    /**
     * 创建一个{@link Node}对象
     * 
     * @return 返回创建一个{@link Node}对象
     */
    Node createNode();
    
    /**
     * 判断指定的{@link Node}对象是否与指定类型匹配
     * @param node {@link Node}对象
     * @param classType 类型
     * @return 如果匹配返回true，否则返回false
     */
    boolean isCompatible(Node node, Class<?> classType);
    
    /**
     * 将{@link Node}对象转为指定类型的对象
     * 
     * @param node {@link Node}对象
     * @param classType 类型
     * @return 返回指定类型的对象
     */
    <T> T convert(Node node, Class<T> classType);
    
    /**
     * 将指定类型的对象转为{@link Node}对象
     * 
     * @param object 指定类型的对象
     * @return 返回{@link Node}对象
     */
    Node convert(Object object);
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    interface Node {

        boolean has(String name);

        Node get(String name);

        Integer getInteger(String name);

        String getString(String name);

        Node[] getArray(String name);

        void put(String name, int value);

        void put(String name, String value);

        void put(String name, Node value);

        void put(String name, Node[] array);

    }
    
}

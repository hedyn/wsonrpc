/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.json.support;

import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.apexes.wsonrpc.core.JsonException;
import net.apexes.wsonrpc.json.JsonImplementor;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class GsonImplementor implements JsonImplementor {
    
    private final Gson gson;
    
    public GsonImplementor() {
        this(new Gson());
    }
    
    public GsonImplementor(Gson gson) {
        this.gson = gson;
    }
    
    @Override
    public Node fromJson(String json) throws JsonException {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new StringReader(json));
        return new GsonNode(jsonElement);
    }

    @Override
    public String toJson(Node node) throws JsonException {
        GsonNode gsonNode = (GsonNode) node;
        return gsonNode.toString();
    }

    @Override
    public Node createNode() {
        return new GsonNode(new JsonObject());
    }

    @Override
    public boolean isCompatible(Node node, Class<?> classType) {
        return true;
    }

    @Override
    public <T> T convert(Node node, Class<T> classType) {
        GsonNode gsonNode = (GsonNode) node;
        return gson.fromJson(gsonNode.jsonElement, classType);
    }
    
    @Override
    public Node convert(Object object) {
        JsonElement jsonElement = gson.toJsonTree(object);
        return new GsonNode(jsonElement);
    }

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class GsonNode implements Node {
        
        private final JsonElement jsonElement;
        private final JsonObject jsonObject;
        
        public GsonNode(JsonElement jsonElement) {
            this.jsonElement = jsonElement;
            if (jsonElement instanceof JsonObject) {
                jsonObject = (JsonObject) jsonElement;
            } else {
                jsonObject = null;
            }
        }

        @Override
        public boolean has(String name) {
            if (jsonObject == null) {
                return false;
            }
            return jsonObject.has(name);
        }

        @Override
        public Node get(String name) {
            if (jsonObject == null) {
                return null;
            }
            return new GsonNode(jsonObject.get(name));
        }

        @Override
        public Integer getInteger(String name) {
            if (jsonObject == null) {
                return null;
            }
            JsonElement je = jsonObject.get(name);
            if (je.isJsonNull()) {
                return null;
            }
            return je.getAsInt();
        }

        @Override
        public String getString(String name) {
            if (jsonObject == null) {
                return null;
            }
            JsonElement je = jsonObject.get(name);
            if (je.isJsonNull()) {
                return null;
            }
            return je.getAsString();
        }

        @Override
        public Node[] getArray(String name) {
            if (jsonObject == null) {
                return null;
            }
            JsonElement je = jsonObject.get(name);
            if (je.isJsonNull()) {
                return new Node[0];
            }
            JsonArray jsonArray = je.getAsJsonArray();
            int size = jsonArray.size();
            Node[] results = new Node[size];
            for (int i = 0; i < size; i++) {
                results[i] = new GsonNode(jsonArray.get(i));
            }
            return results;
        }

        @Override
        public void put(String name, int value) {
            if (jsonObject == null) {
                throw new UnsupportedOperationException();
            }
            jsonObject.addProperty(name, value);
        }

        @Override
        public void put(String name, String value) {
            if (jsonObject == null) {
                throw new UnsupportedOperationException();
            }
            jsonObject.addProperty(name, value);
        }

        @Override
        public void put(String name, Node value) {
            if (jsonObject == null) {
                throw new UnsupportedOperationException();
            }
            if (value instanceof GsonNode) {
                GsonNode gsonNode = (GsonNode) value;
                jsonObject.add(name, gsonNode.jsonElement);
            } else {
                throw new UnsupportedOperationException("value must be GsonNode");
            }
        }

        @Override
        public void put(String name, Node[] array) {
            if (jsonObject == null) {
                throw new UnsupportedOperationException();
            }
            JsonArray jsonArray = new JsonArray();
            for (int i = 0; i < array.length; i++) {
                if (array[i] instanceof GsonNode) {
                    GsonNode gsonNode = (GsonNode) array[i];
                    jsonArray.add(gsonNode.jsonElement);
                } else {
                    throw new UnsupportedOperationException("array[" + i + "] must be GsonNode");
                }
            }
            jsonObject.add(name, jsonArray);
        }

        @Override
        public String toString() {
            return jsonElement.toString();
        }
        
    }

}

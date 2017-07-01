/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.json.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.apexes.wsonrpc.core.JsonException;
import net.apexes.wsonrpc.json.JsonImplementor;

import java.io.IOException;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JacksonImplementor implements JsonImplementor {
    
    private final ObjectMapper objectMapper;
    
    public JacksonImplementor() {
        this(new ObjectMapper());
    }
    
    public JacksonImplementor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Node fromJson(String json) throws JsonException {
        try {
            return new JacksonNode(objectMapper.readTree(json));
        } catch (IOException e) {
            throw new JsonException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public String toJson(Node node) throws JsonException {
        JacksonNode jacksonNode = (JacksonNode) node;
        try {
            return objectMapper.writeValueAsString(jacksonNode.jsonNode);
        } catch (JsonProcessingException e) {
            throw new JsonException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Node createNode() {
        JsonNodeFactory factory = new JsonNodeFactory(false);  
        return new JacksonNode(factory.objectNode());
    }

    @Override
    public boolean isCompatible(Node node, Class<?> classType) {
        return true;
    }

    @Override
    public <T> T convert(Node node, Class<T> classType) {
        JacksonNode jacksonNode = (JacksonNode) node;
        return objectMapper.convertValue(jacksonNode.jsonNode, classType);
    }

    @Override
    public Node convert(Object object) {
        return new JacksonNode(objectMapper.valueToTree(object));
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class JacksonNode implements Node {
        
        private final JsonNode jsonNode;
        
        public JacksonNode(JsonNode jsonNode) {
            this.jsonNode = jsonNode;
        }

        @Override
        public boolean has(String name) {
            return jsonNode.has(name);
        }

        @Override
        public Node get(String name) {
            return new JacksonNode(jsonNode.get(name));
        }

        @Override
        public Integer getInteger(String name) {
            JsonNode jn = jsonNode.get(name);
            if (jn.isNull()) {
                return null;
            }
            return jn.asInt();
        }

        @Override
        public String getString(String name) {
            JsonNode jn = jsonNode.get(name);
            if (jn.isNull()) {
                return null;
            }
            return jn.asText();
        }

        @Override
        public Node[] getArray(String name) {
            JsonNode paramNode = jsonNode.get(name);
            int size = paramNode.size();
            Node[] results = new Node[size];
            for (int i = 0; i < size; i++) {
                results[i] = new JacksonNode(paramNode.get(i));
            }
            return results;
        }

        @Override
        public void put(String name, int value) {
            if (jsonNode instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.put(name, value);
            } else {
                throw new UnsupportedOperationException(jsonNode.getClass().getName());
            }
        }

        @Override
        public void put(String name, String value) {
            if (jsonNode instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.put(name, value);
            } else {
                throw new UnsupportedOperationException(jsonNode.getClass().getName());
            }
        }

        @Override
        public void put(String name, Node value) {
            if (jsonNode instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                if (value instanceof JacksonNode) {
                    JacksonNode jacksonNode = (JacksonNode) value;
                    objectNode.set(name, jacksonNode.jsonNode);
                } else {
                    throw new UnsupportedOperationException("value must be JacksonNode");
                }
            } else {
                throw new UnsupportedOperationException(jsonNode.getClass().getName());
            }
        }

        @Override
        public void put(String name, Node[] array) {
            if (jsonNode instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                ArrayNode arrayNode = objectNode.arrayNode();
                for (int i = 0; i < array.length; i++) {
                    if (array[i] instanceof JacksonNode) {
                        JacksonNode jacksonNode = (JacksonNode) array[i];
                        arrayNode.add(jacksonNode.jsonNode);
                    } else {
                        throw new UnsupportedOperationException("array[" + i + "] must be JacksonNode");
                    }
                }
                objectNode.set(name, arrayNode);
            } else {
                throw new UnsupportedOperationException(jsonNode.getClass().getName());
            }
        }

        @Override
        public String toString() {
            return jsonNode.toString();
        }
    }

}

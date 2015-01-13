package net.apexes.wsonrpc.support;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import net.apexes.wsonrpc.JsonHandler;
import net.apexes.wsonrpc.message.JsonRpcError;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public abstract class AbstractJsonHandler<T> implements JsonHandler {
    
    @Override
    public Throwable convertError(JsonRpcError error) {
        return new Exception(error.getMessage());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object convertResult(Object result, Type type) throws Exception {
        return convertObject((T)result, type);
    }
    
    @Override
    public MethodAndArgs findMethod(Set<Method> methods, Object params) {
        IParams<T> paramsNode = convertParams(params);
        int paramCount = paramsNode.size();
        
        Set<Method> matchedMethods  = new HashSet<Method>();
        for (Method method : methods) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length == paramCount) {
                matchedMethods.add(method);
            }
        }
        
        if (matchedMethods.isEmpty()) {
            return null;
        }
        
        Method bestMethod = null;
        if (matchedMethods.size() == 1 || paramCount == 0) {
            bestMethod = matchedMethods.iterator().next();
        } else {
            if (paramCount == 1) {
                for (Method method : matchedMethods) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (isMatchingType(paramsNode.get(0), paramTypes[0])) {
                        bestMethod = method;
                        break;
                    }
                }
            } else {
                int mostMatches = -1;
                for (Method method : matchedMethods) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    int numMatches = 0;
                    for (int i = 0; i < paramTypes.length; i++) {
                        if (isMatchingType(paramsNode.get(i), paramTypes[i])) {
                            numMatches++;
                        }
                    }
                    if (numMatches > mostMatches) {
                        mostMatches = numMatches;
                        bestMethod = method;
                    }
                }
            }
        }
        
        Class<?>[] paramTypes = bestMethod.getParameterTypes();
        try {
            if (paramCount == 1) {
                Object argument = convertObject(paramsNode.get(0), paramTypes[0]);
                return new MethodAndArgs(bestMethod, argument);
            } else {
                Object[] args = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    args[i] = convertObject(paramsNode.get(i), paramTypes[i]);
                }
                return new MethodAndArgs(bestMethod, args);
            }
        } catch (Exception ex) {
        }
        return null;
    }
    
    protected abstract Object convertObject(T node, Type type) throws Exception;
    
    protected abstract IParams<T> convertParams(Object params);
    
    protected abstract boolean isMatchingType(T node, Class<?> classType);
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    public static interface IParams<T> {
        
        int size();
        
        T get(int index);
        
    }

}

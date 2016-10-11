package net.apexes.wsonrpc.demo.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public final class OnlineClientHolder {
    
    private static Map<String, String> sessionMap = new HashMap<String, String>();
    private static Map<String, String> idMap = new HashMap<String, String>();
    
    public static void register(String clientId, String sessionId) {
        sessionMap.put(clientId, sessionId);
        idMap.put(sessionId, clientId);
    }
    
    public static void unregister(String sessionId) {
        String clientId = idMap.remove(sessionId);
        sessionMap.remove(clientId);
    }
    
    public static String getSessionId(String posId) {
        return sessionMap.get(posId);
    }
    
    public static Set<String> getOnlines() {
        return sessionMap.keySet();
    }

    private OnlineClientHolder() {}
}

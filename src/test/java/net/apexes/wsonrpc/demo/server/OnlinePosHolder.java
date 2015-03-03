package net.apexes.wsonrpc.demo.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 存储在线的POS（仅作示范用）
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public final class OnlinePosHolder {
    
    private static Map<String, String> sessionMap = new HashMap<String, String>();
    
    public static void registerPosId(String posId, String sessionId) {
        sessionMap.put(posId, sessionId);
    }
    
    public static String getSessionId(String posId) {
        return sessionMap.get(posId);
    }
    
    public static Set<String> getOnlines() {
        return sessionMap.keySet();
    }

    private OnlinePosHolder() {}
}

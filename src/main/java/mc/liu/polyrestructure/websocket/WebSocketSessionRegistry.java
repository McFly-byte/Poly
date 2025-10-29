// src/main/java/mc/liu/polyrestructure/websocket/WebSocketSessionRegistry.java
package mc.liu.polyrestructure.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionRegistry { // 路由表：记录 某某任务ID下挂着哪些ws会话
    // taskId -> set of sessions
    private final Map<String, Set<WebSocketSession>> map = new ConcurrentHashMap<>();

    public void add(String taskId, WebSocketSession session) { // 把session加入某 task 的订阅集合
        map.compute(taskId, (k, set) -> {
            if (set == null) set = Collections.newSetFromMap(new ConcurrentHashMap<>());
            set.add(session);
            return set;
        });
    }

    public void removeSession(WebSocketSession session) { // 从所有 task 集合里移除这个session
        for (Map.Entry<String, Set<WebSocketSession>> e : map.entrySet()) {
            Set<WebSocketSession> s = e.getValue();
            s.remove(session);
        }
    }

    public void sendToTaskSessions(String taskId, TextMessage msg) { // 向该 task 的所有session群发
        Set<WebSocketSession> sessions = map.get(taskId);
        if (sessions == null) return;
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) session.sendMessage(msg);
            } catch (IOException ex) {
                // ignore or log
            }
        }
    }
}

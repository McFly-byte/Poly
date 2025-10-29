// src/main/java/mc/liu/polyrestructure/websocket/ProgressWebSocketHandler.java
package mc.liu.polyrestructure.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class ProgressWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionRegistry registry;

    public ProgressWebSocketHandler(WebSocketSessionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Expect client to connect with ?taskId=...
        URI uri = session.getUri();
        if (uri != null && uri.getQuery() != null) {
            String q = uri.getQuery();
            for (String part : q.split("&")) {
                if (part.startsWith("taskId=")) {
                    String taskId = part.substring("taskId=".length());
                    registry.add(taskId, session);
                    return;
                }
            }
        }
        // if no taskId given, close
        session.close(CloseStatus.BAD_DATA);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        registry.removeSession(session);
    }
}

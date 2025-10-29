// src/main/java/mc/liu/polyrestructure/config/RedisProgressSubscriber.java
package mc.liu.polyrestructure.config;

import mc.liu.polyrestructure.websocket.WebSocketSessionRegistry;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
public class RedisProgressSubscriber implements MessageListener {

    private final WebSocketSessionRegistry registry;

    public RedisProgressSubscriber(WebSocketSessionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel()); // 发布频道名
        String body = new String(message.getBody()); // 载荷（JSON文本
        // channel format "progress:{taskId}"
        String taskId = channel.substring(channel.indexOf(':') + 1);
        // forward to all sessions subscribed to this taskId
        registry.sendToTaskSessions(taskId, new TextMessage(body)); // 把 JSON 文本推送给所有订阅此任务的 WebSocket 会话。
    }
}

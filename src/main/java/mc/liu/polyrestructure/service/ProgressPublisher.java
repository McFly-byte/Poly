// src/main/java/mc/liu/polyrestructure/service/ProgressPublisher.java
package mc.liu.polyrestructure.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProgressPublisher {
    private final StringRedisTemplate redis;

    public ProgressPublisher(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void publish(String taskId, int percent, int processedRows, int totalRows) {
        String key = "progress:" + taskId;
        String payload = String.format(
                "{\"taskId\":\"%s\",\"percent\":%d,\"processedRows\":%d,\"totalRows\":%d}",
                taskId, percent, processedRows, totalRows
        );
        redis.convertAndSend(key, payload); // 调用Redis的Pub/Sub发布功能，所有订阅了匹配 progress:* 的客户端（即 RedisMessageListenerContainer）都会立即收到；
        redis.opsForValue().set(key, payload); // 把当前进度也存到 Redis 的键值对中。这样可以让新连接的 WebSocket 立刻读取最新进度（即使错过了中途的广播）；
    }
}

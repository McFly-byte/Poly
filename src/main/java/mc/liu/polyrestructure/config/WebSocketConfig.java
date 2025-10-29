// src/main/java/mc/liu/polyrestructure/config/WebSocketConfig.java
package mc.liu.polyrestructure.config;

import mc.liu.polyrestructure.websocket.ProgressWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ProgressWebSocketHandler handler;

    public WebSocketConfig(ProgressWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/progress")
                .setAllowedOrigins("*"); // dev 用，生产应指定 origin
    }
}

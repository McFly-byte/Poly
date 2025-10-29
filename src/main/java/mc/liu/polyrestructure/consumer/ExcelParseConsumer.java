// src/main/java/mc/liu/polyrestructure/consumer/ExcelParseConsumer.java
package mc.liu.polyrestructure.consumer;

import mc.liu.polyrestructure.dto.ParseTaskDto;
import mc.liu.polyrestructure.service.ExcelParserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ExcelParseConsumer {

    private final ExcelParserService parserService;

    public ExcelParseConsumer(ExcelParserService parserService) {
        this.parserService = parserService;
    }

    @RabbitListener(
            queues = "excel.parse.queue",
            autoStartup = "${app.parse-listener.enabled:true}"  // 新增：允许通过配置关闭
    )
    public void onMessage(ParseTaskDto dto) {
        String taskId = dto.getTaskId();
        try {
            parserService.handleParseTask(taskId, dto.getObjectKey(), dto.getFileHash());
            // if successful, message auto-acked
        } catch (Exception e) {
            // Log and let the message be requeued or go to DLQ depending on RabbitMQ config.
            // For simplicity, print stack
            e.printStackTrace();
            // rethrow to allow Rabbit to retry / DLQ
            throw new RuntimeException(e);
        }
    }
}

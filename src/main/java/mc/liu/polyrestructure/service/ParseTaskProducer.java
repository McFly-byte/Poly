package mc.liu.polyrestructure.service;

import mc.liu.polyrestructure.dto.ParseTaskDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ParseTaskProducer {
    private final RabbitTemplate rabbitTemplate;

    public ParseTaskProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendParseTask(ParseTaskDto dto) {
        rabbitTemplate.convertAndSend("excel.exchange", "excel.parse", dto);
    }
}

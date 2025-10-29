package mc.liu.polyrestructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "excel.exchange";
    public static final String QUEUE = "excel.parse.queue";
    public static final String ROUTING_KEY = "excel.parse";

    @Bean
    public DirectExchange excelExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue excelQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding excelBinding(Queue excelQueue, DirectExchange excelExchange) {
        return BindingBuilder.bind(excelQueue).to(excelExchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}

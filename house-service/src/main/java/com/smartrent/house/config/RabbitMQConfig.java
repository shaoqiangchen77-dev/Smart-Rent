package com.smartrent.house.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_HOUSE = "house.exchange";
    public static final String QUEUE_HOUSE_UPDATE = "house.update.queue";
    public static final String QUEUE_BILL_OVERDUE = "bill.overdue.queue";
    public static final String ROUTING_HOUSE_UPDATE = "house.update";
    public static final String ROUTING_BILL_OVERDUE = "bill.overdue";

    @Bean
    public DirectExchange houseExchange() {
        return new DirectExchange(EXCHANGE_HOUSE);
    }

    @Bean
    public Queue houseUpdateQueue() {
        return QueueBuilder.durable(QUEUE_HOUSE_UPDATE).build();
    }

    @Bean
    public Queue billOverdueQueue() {
        return QueueBuilder.durable(QUEUE_BILL_OVERDUE).build();
    }

    @Bean
    public Binding houseUpdateBinding() {
        return BindingBuilder.bind(houseUpdateQueue()).to(houseExchange()).with(ROUTING_HOUSE_UPDATE);
    }

    @Bean
    public Binding billOverdueBinding() {
        return BindingBuilder.bind(billOverdueQueue()).to(houseExchange()).with(ROUTING_BILL_OVERDUE);
    }
}

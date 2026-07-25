package com.smartrent.house.mq;

import com.smartrent.house.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HouseUpdateProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送房源更新消息（用于ES索引同步）
     */
    public void sendHouseUpdate(Long houseId, String action) {
        Map<String, Object> message = Map.of("houseId", houseId, "action", action);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_HOUSE, RabbitMQConfig.ROUTING_HOUSE_UPDATE, message);
        log.info("发送房源更新消息: houseId={}, action={}", houseId, action);
    }

    /**
     * 发送账单逾期消息
     */
    public void sendBillOverdue(Long billId, Long userId) {
        Map<String, Object> message = Map.of("billId", billId, "userId", userId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_HOUSE, RabbitMQConfig.ROUTING_BILL_OVERDUE, message);
        log.info("发送账单逾期消息: billId={}, userId={}", billId, userId);
    }
}

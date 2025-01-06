package com.trip.kafka;

import com.trip.kafka.code.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = KafkaTopics.MEMBER_EVENTS, groupId = "member-service")
    public void consumeMessage(String message) {
        System.out.println("Consume message from topic: " + message);
    }
}

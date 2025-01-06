package com.trip.kafka;

import com.trip.kafka.code.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    public void sendUserEvent(KafkaEvent kafkaEvent) {
        kafkaTemplate.send(KafkaTopics.MEMBER_EVENTS, kafkaEvent);
    }

}

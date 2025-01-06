package com.trip.config;

import com.trip.kafka.code.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic createTopic() {
        return new NewTopic(KafkaTopics.MEMBER_EVENTS, 1, (short) 1);
    }
}

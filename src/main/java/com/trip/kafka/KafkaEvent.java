package com.trip.kafka;

import com.trip.kafka.code.KafkaActions;
import com.trip.user.dto.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class KafkaEvent {
    private KafkaActions action;
    private User beforeUser;
    private User afterUser;
    private String searchKeyword;
    private LocalDateTime timestamp;
}

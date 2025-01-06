package com.trip.kafka.code;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum KafkaActions {
    CREATED("created"),

    GET_ALL_USERS("getAllUsers"),
    GET_USERS_BY_IDX("getUsersByIdx"),
    GET_USERS_BY_EMAIL("getUsersByEmail"),
    GET_USERS_BY_NAME("getUsersByName"),

    UPDATED("updated"),
    PASSWORD_UPDATED("passwordUpdated"),
    DELETED("deleted"),

    LOGIN("login"),
    LOGOUT("logout"),
    REFRESH("refresh");


    private final String action;

}
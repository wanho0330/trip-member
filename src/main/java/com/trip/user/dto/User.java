package com.trip.user.dto;

import com.trip.user.code.Status;
import com.trip.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class User {
    private Long idx;
    private String email;
    private String name;
    // 비밀번호를 전달하지 않도록 설계
    private Status status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String lastLoginIp;
    private LocalDateTime lastLoginAt;
    private int failedAttempts;

    public static User formEntity(UserEntity userEntity) {
        UserBuilder builder = User.builder();
        builder.email(userEntity.getEmail())
                .name(userEntity.getName())
                .status(userEntity.getStatus())
                .createAt(userEntity.getCreatedAt())
                .updateAt(userEntity.getUpdatedAt())
                .lastLoginIp(userEntity.getLastLoginIp())
                .lastLoginAt(userEntity.getLastLoginAt())
                .failedAttempts(userEntity.getFailedAttempts());

        if (userEntity.getIdx() != null) {
            builder.idx(userEntity.getIdx());
        }

        return builder.build();
    }

    public static List<User> fromEntityList(List<UserEntity> userEntityList) {
        return userEntityList.stream().
                map(User::formEntity).
                collect(Collectors.toList());
    }

    public UserEntity toEntity() {
        return UserEntity.builder()
                .email(this.email)
                .name(this.name)
                .status(this.status)
                .lastLoginAt(this.lastLoginAt)
                .lastLoginIp(this.lastLoginIp)
                .failedAttempts(this.failedAttempts)
                .build();
    }

    public UserEntity toEntity(String password) {
        return UserEntity.builder()
                .email(this.email)
                .password(password)
                .name(this.name)
                .status(this.status)
                .lastLoginAt(this.lastLoginAt)
                .lastLoginIp(this.lastLoginIp)
                .failedAttempts(this.failedAttempts)
                .build();
    }

}

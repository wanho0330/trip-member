package com.trip.user.entity;

import com.trip.user.code.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class UserEntity {
    /**
     * 인덱스
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    /**
     * 이메일
     */
    @Column(nullable = false, length = 255, unique = true, updatable = false)
    private String email;

    /**
     * 비밀번호
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 이름
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 유저 상태
     * {@code ACTIVE}: 활성화, {@code INACTIVE}: 비활성화, {@code SUSPEND}: 일시정지, {@code DELETED}: 삭제
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Status status = Status.ACTIVE;

    /**
     * 생성일자
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * 정보수정 일자
     */
    @Column(name = "updated_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * 마지막 로그인 아이피
     */
    @Column(name = "last_login_ip", length = 16)
    private String lastLoginIp; //

    /**
     * 마지막 로그인 시간
     */
    @Column(name = "last_login_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastLoginAt;

    /**
     * 로그인 실패 횟수
     */
    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts = 0;

    @Builder
    public UserEntity(String email, String password, String name, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String lastLoginIp, LocalDateTime lastLoginAt, int failedAttempts) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginIp = lastLoginIp;
        this.lastLoginAt = lastLoginAt;
        this.failedAttempts = failedAttempts;
    }
}

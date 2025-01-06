package com.trip.user.repository;

import com.trip.user.entity.UserEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserCustomRepository {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByIdx(Long idx);
    boolean existsByIdx(Long idx);

    List<UserEntity> findByEmailStartsWith(Long cursor, String email, Pageable pageable);
    List<UserEntity> findByNameStartsWith(Long cursor,String name, Pageable pageable);
    List<UserEntity> findAllByOrderByIdxDesc(Long cursor, Pageable pageable);
}

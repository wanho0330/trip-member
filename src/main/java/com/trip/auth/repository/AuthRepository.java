package com.trip.auth.repository;

import com.trip.auth.entity.TokenEntity;
import org.springframework.data.repository.CrudRepository;

public interface AuthRepository extends CrudRepository<TokenEntity, String> {

    boolean existsByRefreshToken(String refreshToken);
    TokenEntity findByRefreshToken(String refreshToken);


}

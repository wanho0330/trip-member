package com.trip.user.repository;

import com.trip.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserCustomRepository {

}
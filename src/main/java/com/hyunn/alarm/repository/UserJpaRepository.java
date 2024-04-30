package com.hyunn.alarm.repository;

import com.hyunn.alarm.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

  Optional<User> findUserByPhone(String phone);

  boolean existsUserByPhone(String phone);

}

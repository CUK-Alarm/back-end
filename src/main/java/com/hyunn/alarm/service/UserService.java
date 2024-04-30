package com.hyunn.alarm.service;

import com.hyunn.alarm.dto.request.UserDepartmentRequest;
import com.hyunn.alarm.dto.request.UserEmailRequest;
import com.hyunn.alarm.entity.User;
import com.hyunn.alarm.exception.UserNotFoundException;
import com.hyunn.alarm.repository.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserJpaRepository userJpaRepository;

  /**
   * 학과 업데이트
   */
  public String updateDepartment(UserDepartmentRequest userDepartmentRequest) {

    String major = userDepartmentRequest.getMajor();
    String minor = userDepartmentRequest.getMinor();
    String phone = userDepartmentRequest.getPhone();

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    User existUser = user.get();
    existUser.updateDepartment(major, minor);
    userJpaRepository.save(existUser);
    return "전공 : " + major + ", 부전공 : " + minor;
  }

  /**
   * 이메일 업데이트
   */
  public String updateEmail(UserEmailRequest userEmailRequest) {

    String email = userEmailRequest.getEmail();
    String phone = userEmailRequest.getPhone();

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    User existUser = user.get();
    existUser.updateEmail(email);
    userJpaRepository.save(existUser);
    return "이메일 변경 : " + email;
  }

  /**
   * 유저 삭제
   */
  public void deleteUser(String phone) {

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    User existUser = user.get();
    userJpaRepository.delete(existUser);
  }
}

package com.hyunn.alarm.service;

import com.hyunn.alarm.dto.request.UserDepartmentRequest;
import com.hyunn.alarm.dto.request.UserEmailRequest;
import com.hyunn.alarm.entity.User;
import com.hyunn.alarm.exception.UnauthorizedException;
import com.hyunn.alarm.exception.UserNotFoundException;
import com.hyunn.alarm.repository.UserJpaRepository;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserJpaRepository userJpaRepository;
  private final MessageService messageService;

  /**
   * 학과 업데이트
   */
  @Transactional
  public String updateDepartment(UserDepartmentRequest userDepartmentRequest) {

    String major = userDepartmentRequest.getMajor();
    String minor = userDepartmentRequest.getMinor();
    String phone = userDepartmentRequest.getPhone();

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    User existUser = user.get();

    if (major.equals(minor)) {
      existUser.updateDepartment(major, "null");
      userJpaRepository.save(existUser);
      return "전공 심화 : " + major;
    }

    existUser.updateDepartment(major, minor);
    userJpaRepository.save(existUser);
    return "제 1전공 : " + major + ", 제 2전공 : " + minor;
  }

  /**
   * 이메일 업데이트
   */
  @Transactional
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
  @Transactional
  public void deleteUser(String phone) {

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    User existUser = user.get();
    userJpaRepository.delete(existUser);
  }

  /**
   * 유저 인증 메세지
   */
  @Transactional
  public String sendAuthentication(String phone) throws IOException {

    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    User existUser = user.get();
    existUser.updateCode(generateRandomNumber());
    userJpaRepository.save(existUser);
    String message = messageService.sendAuthenticationMessage(phone, existUser.getCode());
    return message;
  }

  /**
   * 랜덤 인증 코드 생성기
   */
  public String generateRandomNumber() {
    Random random = new Random();
    int randomNumber = 100000 + random.nextInt(900000); // 6자리 숫자 생성 (100000 ~ 999999)
    return String.valueOf(randomNumber);
  }

  public String authentication(String phone, String code) {
    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    if (!user.get().getCode().equals(code)) {
      throw new UnauthorizedException("인증 코드가 올바르지 않습니다.");
    }
    return "인증 완료";
  }
}

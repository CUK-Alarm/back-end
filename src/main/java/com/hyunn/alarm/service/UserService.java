package com.hyunn.alarm.service;

import com.hyunn.alarm.dto.request.UserDepartmentRequest;
import com.hyunn.alarm.dto.request.UserEmailRequest;
import com.hyunn.alarm.entity.User;
import com.hyunn.alarm.exception.ApiKeyNotValidException;
import com.hyunn.alarm.exception.ApiNotFoundException;
import com.hyunn.alarm.exception.UnauthorizedException;
import com.hyunn.alarm.exception.UserNotFoundException;
import com.hyunn.alarm.repository.UserJpaRepository;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  @Value("${spring.security.x-api-key}")
  private String xApiKey;

  private final UserJpaRepository userJpaRepository;
  private final MessageService messageService;

  /**
   * 학과 업데이트
   */
  @Transactional
  public String updateDepartment(UserDepartmentRequest userDepartmentRequest, String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 요청 바디에서 정보 파싱
    String major = userDepartmentRequest.getMajor();
    String minor = userDepartmentRequest.getMinor();
    String phone = userDepartmentRequest.getPhone();

    // 유저 탐색
    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
    User existUser = user.get();

    // 전공과 부전공이 같다면 전공 심화로 처리
    if (major.equals(minor)) {
      existUser.updateDepartment(major, "null");
      userJpaRepository.save(existUser);
      return "전공 심화 : " + major;
    }

    // 변경된 전공, 부전공 업데이트 후 저장
    existUser.updateDepartment(major, minor);
    userJpaRepository.save(existUser);
    return "제 1전공 : " + major + ", 제 2전공 : " + minor;
  }

  /**
   * 이메일 업데이트
   */
  @Transactional
  public String updateEmail(UserEmailRequest userEmailRequest, String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 요청 바디에서 정보 추출
    String email = userEmailRequest.getEmail();
    String phone = userEmailRequest.getPhone();

    // 유저 탐색
    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
    User existUser = user.get();

    // 이메일 업데이트 및 저장
    existUser.updateEmail(email);
    userJpaRepository.save(existUser);
    return "이메일이 변경되었습니다.\n " + email;
  }

  /**
   * 유저 삭제
   */
  @Transactional
  public String deleteUser(String phone, String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 유저 탐색
    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
    User existUser = user.get();

    // 유저 삭제 및 저장
    userJpaRepository.delete(existUser);
    return existUser.getNickName() + "님의 탈퇴가 완료되었습니다.";
  }

  /**
   * 유저 인증 메세지
   */
  @Transactional
  public String sendAuthentication(String phone, String apiKey) throws IOException {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 서비스를 등록한 유저인지 확인한다 => 악용 대비
    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
    User existUser = user.get();

    // 인증 번호 부여 및 저장
    existUser.updateCode(generateRandomNumber());
    userJpaRepository.save(existUser);

    // 유저에게 인증 번호 발송 (문자)
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

  /**
   * 인증 번호 확인 로직
   */
  public String authentication(String phone, String code, String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 유저 탐색
    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));

    // 인증 코드가 같지 않다면 오류 처리
    if (!user.get().getCode().equals(code)) {
      throw new UnauthorizedException("인증 코드가 올바르지 않습니다.");
    }
    return "인증 완료";
  }

  /**
   * 유료 서비스 등록 및 해지
   */
  @Transactional
  public String updateRole(String phone, String role, String apiKey) {
    // API KEY 유효성 검사
    if (apiKey == null || !apiKey.equals(xApiKey)) {
      throw new ApiKeyNotValidException("API KEY가 올바르지 않습니다.");
    }

    // 등록되지 않는 명령어 입력시 오류
    if (!(role.equals("root") || role.equals("user"))) {
      throw new ApiNotFoundException("유효하지 않은 명령어입니다.");
    }

    // 유저 탐색
    Optional<User> user = Optional.ofNullable(
        userJpaRepository.findUserByPhone(phone)
            .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
    User existUser = user.get();

    // 유저 서비스 단계 업데이트 및 저장
    existUser.updateRole(role);
    userJpaRepository.save(existUser);

    // root 유저일 경우 -> 문자 서비스 제공을 위해서 upgrade로 분류
    boolean upgrade = false;
    if (role.equals("root")) {
      upgrade = true;
    }

    // 응답 메세지
    String message = existUser.getNickName() + " 님께서 일반 유저로 변경되었습니다.";
    if (upgrade) {
      message = existUser.getNickName() + " 님께서 유료 서비스 대상자로 변경되었습니다.";
    }

    return message;
  }
}

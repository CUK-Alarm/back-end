package com.hyunn.alarm.controller;

import com.hyunn.alarm.dto.request.UserDepartmentRequest;
import com.hyunn.alarm.dto.request.UserEmailRequest;
import com.hyunn.alarm.dto.response.ApiStandardResponse;
import com.hyunn.alarm.service.UserService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;

  /**
   * 유저에게 인증 메세지 보내기
   */
  @PostMapping("/code")
  public ResponseEntity<ApiStandardResponse<String>> sendAuthentication(
      @RequestParam("phone") String phone) throws IOException {
    String message = userService.sendAuthentication(phone);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

  /**
   * 인증 코드가 맞는지 확인하기
   */
  @PostMapping("/authentication")
  public ResponseEntity<ApiStandardResponse<String>> authentication(
      @RequestParam("phone") String phone, @RequestParam("code") String code) {
    String message = userService.authentication(phone, code);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

  /**
   * 전공 알림 설정
   */
  @PostMapping("/department")
  public ResponseEntity<ApiStandardResponse<String>> updateDepartment(
      @Valid @RequestBody UserDepartmentRequest userDepartmentRequest) {
    String message = userService.updateDepartment(userDepartmentRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

  /**
   * 이메일 수정
   */
  @PostMapping("/email")
  public ResponseEntity<ApiStandardResponse<String>> updateEmail(
      @Valid @RequestBody UserEmailRequest userEmailRequest) {
    String message = userService.updateEmail(userEmailRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

  /**
   * 유저 삭제
   */
  @DeleteMapping()
  public ResponseEntity<ApiStandardResponse<String>> deleteUser(
      @RequestParam("phone") String phone) {
    userService.deleteUser(phone);
    return ResponseEntity.ok(ApiStandardResponse.success("탈퇴 성공"));
  }

}

package com.hyunn.alarm.controller;

import com.hyunn.alarm.dto.request.UserEmailRequest;
import com.hyunn.alarm.dto.response.ApiStandardResponse;
import com.hyunn.alarm.dto.request.UserDepartmentRequest;
import com.hyunn.alarm.service.UserService;
import jakarta.validation.Valid;
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

  @PostMapping("/department")
  public ResponseEntity<ApiStandardResponse<String>> updateDepartment(
      @Valid @RequestBody UserDepartmentRequest userDepartmentRequest) {
    String message = userService.updateDepartment(userDepartmentRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

  @PostMapping("/email")
  public ResponseEntity<ApiStandardResponse<String>> updateEmail(
      @Valid @RequestBody UserEmailRequest userEmailRequest) {
    String message = userService.updateEmail(userEmailRequest);
    return ResponseEntity.ok(ApiStandardResponse.success(message));
  }

  @DeleteMapping()
  public ResponseEntity<ApiStandardResponse<String>> deleteUser(
      @RequestParam("phone") String phone) {
    userService.deleteUser(phone);
    return ResponseEntity.ok(ApiStandardResponse.success("탈퇴 성공"));
  }

}

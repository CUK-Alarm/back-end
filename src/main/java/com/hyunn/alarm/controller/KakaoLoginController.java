package com.hyunn.alarm.controller;

import com.hyunn.alarm.dto.response.ApiStandardResponse;
import com.hyunn.alarm.dto.response.UserResponse;
import com.hyunn.alarm.service.KakaoLoginService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class KakaoLoginController {

  private final KakaoLoginService kakaoLoginService;

  @GetMapping("/oauth2/code/kakao")
  public ResponseEntity<ApiStandardResponse<UserResponse>> kakaoLogin(
      @RequestParam("code") String code) {
    String accessToken = kakaoLoginService.getAccessToken(code);
    UserResponse userResponse = kakaoLoginService.getUserInfo(accessToken);
    return ResponseEntity.ok(ApiStandardResponse.success(userResponse));
  }

}

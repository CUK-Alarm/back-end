package com.hyunn.alarm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunn.alarm.service.KakaoLoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class KakaoLoginController {

  private final KakaoLoginService kakaoLoginService;

  /**
   * 카카오 로그인
   */
  @GetMapping("/api/login/oauth2/code/kakao")
  public String kakaoLogin(@RequestParam("code") String code,
      RedirectAttributes redirectAttributes) {
    String accessToken = kakaoLoginService.getAccessToken(code);
    kakaoLoginService.getUserInfo(accessToken, redirectAttributes);
    return "redirect:/main";
  }

  /**
   * 위 Controller의 JSON을 model로 입력받아서 웹에 표시
   */
  @GetMapping("/main")
  public String mainPage(HttpServletRequest request, Model model) throws JsonProcessingException {
    kakaoLoginService.mainPage(request, model);
    return "main";
  }
}

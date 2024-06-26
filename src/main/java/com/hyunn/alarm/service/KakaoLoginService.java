package com.hyunn.alarm.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hyunn.alarm.dto.response.UserResponse;
import com.hyunn.alarm.entity.User;
import com.hyunn.alarm.exception.ApiNotFoundException;
import com.hyunn.alarm.exception.UserNotFoundException;
import com.hyunn.alarm.repository.UserJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

  @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
  private String clientSecret;

  @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
  private String kakaoRedirectUri;

  @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
  private String authorizationGrantType;

  @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
  private String tokenUri;

  @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
  private String userInfoUri;

  private final UserJpaRepository userJpaRepository;

  /**
   * 엑세스 토큰 받아오기
   */
  public String getAccessToken(String code) {
    RestTemplate restTemplate = new RestTemplate();

    //HttpHeader 오브젝트
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    //HttpBody 오브젝트
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", authorizationGrantType);
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("redirect_uri", kakaoRedirectUri);
    params.add("code", code);

    //http 바디(params)와 http 헤더(headers)를 가진 엔티티
    HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
        new HttpEntity<>(params, headers);

    //reqUrl로 Http 요청 , POST 방식
    ResponseEntity<String> response =
        restTemplate.exchange(tokenUri, HttpMethod.POST, kakaoTokenRequest, String.class);

    // API 응답을 처리합니다.
    if (response.getStatusCode().is2xxSuccessful()) {
      // 성공적으로 API를 호출한 경우의 처리
      System.out.println("API 호출 성공: " + response.getBody());
    } else {
      // API 호출이 실패한 경우의 처리
      System.out.println("API 호출 실패: " + response.getStatusCode());
      throw new ApiNotFoundException("API 호출에 문제가 생겼습니다.");
    }

    // JSON 파싱
    String responseBody = response.getBody();
    if (responseBody == null || responseBody.isEmpty()) {
      throw new ApiNotFoundException("API 응답이 비어 있습니다.");
    }

    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
    String accessToken = jsonObject.get("access_token").getAsString();

    return accessToken;
  }

  /**
   * 로그인한 유저 정보 받아오기 -> 저장하기
   */
  @Transactional
  public UserResponse getUserInfo(String accessToken, RedirectAttributes redirectAttributes) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    //http 헤더(headers)를 가진 엔티티
    HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
        new HttpEntity<>(headers);

    //reqUrl로 Http 요청 , POST 방식
    ResponseEntity<String> response =
        restTemplate.exchange(userInfoUri, HttpMethod.POST, kakaoProfileRequest, String.class);

    // API 응답을 처리합니다.
    if (response.getStatusCode().is2xxSuccessful()) {
      // 성공적으로 API를 호출한 경우의 처리
      System.out.println("API 호출 성공: " + response.getBody());
    } else {
      // API 호출이 실패한 경우의 처리
      System.out.println("API 호출 실패: " + response.getStatusCode());
      throw new ApiNotFoundException("API 호출에 문제가 생겼습니다.");
    }

    // JSON 파싱 후 오류 처리
    String responseBody = response.getBody();
    if (responseBody == null || responseBody.isEmpty()) {
      throw new ApiNotFoundException("API 응답이 비어 있습니다.");
    }

    // JsonObject 정보 파싱
    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
    String email = jsonObject.getAsJsonObject("kakao_account").get("email").getAsString();
    String nickname = jsonObject.getAsJsonObject("kakao_account").getAsJsonObject("profile")
        .get("nickname").getAsString();
    String phoneNum = jsonObject.getAsJsonObject("kakao_account").get("phone_number")
        .getAsString();

    // +82 10-1234-5678 -> 01012345678 변환
    String phone = extractNumber(phoneNum);
    phone = phone.substring(2, 12);
    phone = "0" + phone;

    // 이미 회원가입을 한 유저라면 바로 처리
    if (userJpaRepository.existsUserByPhone(phone)) {
      Optional<User> user = Optional.ofNullable(
          userJpaRepository.findUserByPhone(phone)
              .orElseThrow(() -> new UserNotFoundException("유저 정보를 가져오지 못했습니다.")));
      User existUser = user.get();
      existUser.updateAccessToken(accessToken);
      userJpaRepository.save(existUser);

      // th문을 위한 변환
      String major = null;
      String minor = null;
      String role = null;

      // Major가 null이 아니라면 major로 설정
      if (!existUser.getMajor().equals("null")) {
        major = existUser.getMajor();
      }
      // Minor가 null이 아니라면 minor로 설정 -> null이면 전공심화로 판단함.
      if (!existUser.getMinor().equals("null")) {
        minor = existUser.getMinor();
      }
      // root 계정이라면 root로 변경
      if (existUser.getRole().equals("root")) {
        role = existUser.getRole();
      }

      // UserResponse 생성
      UserResponse userResponse = UserResponse.create(existUser.getNickName(), existUser.getEmail(),
          formatPhoneNumber(existUser.getPhone()), major, minor, role);

      // 모델에 상속함
      redirectAttributes.addFlashAttribute("user", userResponse);

      return userResponse;
    }

    // 새로운 유저라면 객체를 새로 만들고 저장
    User newUser = User.createUser(nickname, email, phone, "null", "null", accessToken);
    userJpaRepository.save(newUser);

    UserResponse userResponse = UserResponse.create(nickname, email, formatPhoneNumber(phone), null,
        null, null);

    redirectAttributes.addFlashAttribute("user", userResponse);

    return userResponse;
  }

  /**
   * 핸드폰 번호 파싱 함수
   */
  public static String extractNumber(String input) {
    StringBuilder sb = new StringBuilder();
    for (char c : input.toCharArray()) {
      if (Character.isDigit(c)) {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * 핸드폰 형식 변환
   */
  public static String formatPhoneNumber(String phoneNumber) {
    phoneNumber = phoneNumber.replaceAll("\\D", "");
    return phoneNumber.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
  }

  /**
   * 모델 상속
   */
  public void mainPage(HttpServletRequest request, Model model) {
    Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
    UserResponse response = null;
    if (inputFlashMap != null) {
      response = (UserResponse) inputFlashMap.get("user");
    }
    model.addAttribute("user", response);
  }
}


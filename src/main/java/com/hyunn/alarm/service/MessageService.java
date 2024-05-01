package com.hyunn.alarm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunn.alarm.entity.Department;
import com.hyunn.alarm.entity.User;
import com.hyunn.alarm.exception.ApiNotFoundException;
import com.hyunn.alarm.repository.DepartmentJpaRepository;
import com.hyunn.alarm.repository.UserJpaRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

  @Value("${spring.mail.username}")
  private String mailHost;

  @Value("${sendm.call-no}")
  private String senderNum;

  @Value("${sendm.message-uri}")
  private String sendmMessageUri;

  @Value("${sendm.master-id}")
  private String masterUserId;

  @Value("${sendm.api-key}")
  private String sendmApiKey;

  private final DepartmentJpaRepository departmentJpaRepository;
  private final JavaMailSender javaMailSender;


  /**
   * 이메일 발송
   */
  public String sendSimpleMail(String message, String email, String major, String minor) {
    if (!major.equals("null")) {
      Department major_department = departmentJpaRepository.findByMajor(major);
      message += major + "\n" + major_department.getNotification() + "\n";
    }

    if (!minor.equals("null")) {
      Department minor_depart = departmentJpaRepository.findByMajor(minor);
      message += minor + "\n" + minor_depart.getNotification() + "\n";
    }

    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

    // 발신자
    simpleMailMessage.setFrom(mailHost);

    // 수신자
    simpleMailMessage.setTo(email);

    // 제목
    String title = "[CUK 알리미] 오늘의 공지사항";
    simpleMailMessage.setSubject(title);

    // 내용
    message += "자세한 내용은 학교 홈페이지에서 확인해주세요!\nhttps://www.catholic.ac.kr/index.do";
    simpleMailMessage.setText(message);

    try {
      javaMailSender.send(simpleMailMessage);
      return "이메일 전송 성공! " + email;
    } catch (MailException e) {
      return "이메일 전송 실패: " + e.getMessage();
    }
  }

  /**
   * 문자 메세지 발송 -> HttpURLConnection 사용 (LMS 가격으로 보류)
   */
  public String sendSMS(String message, String phone, String major, String minor)
      throws IOException {
    if (!major.equals("null")) {
      Department major_department = departmentJpaRepository.findByMajor(major);
      message += major + "\n" + major_department.getNotification() + "\n";
    }

    if (!minor.equals("null")) {
      Department minor_depart = departmentJpaRepository.findByMajor(minor);
      message += minor + "\n" + minor_depart.getNotification() + "\n";
    }

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("callerNo", senderNum);

    // 메세지
    String title = "[CUK 알리미] ";
    requestBody.put("title", title);

    // 내용
    message += "자세한 내용은 학교 홈페이지에서 확인해주세요!\nhttps://www.catholic.ac.kr/index.do";
    requestBody.put("message", message);

    String receiveNum =
        phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7);
    requestBody.put("receiveNos", receiveNum);

    // 요청을 보낼 URL 생성
    URL url = new URL(sendmMessageUri);

    // Open a connection through the URL
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    // 요청 방법 설정
    connection.setRequestMethod("POST");

    // 요청 헤더 설정
    connection.setRequestProperty("user-id", masterUserId);
    connection.setRequestProperty("api-key", sendmApiKey);
    connection.setRequestProperty("Content-Type", "application/json");

    // 요청 본문 설정
    connection.setDoOutput(true);
    try (OutputStream os = connection.getOutputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      mapper.writeValue(os, requestBody);
    }

    // 응답 처리
    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      // 성공적으로 API를 호출한 경우의 처리
      try (BufferedReader in = new BufferedReader(
          new InputStreamReader(connection.getInputStream()))) {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
          response.append(line);
        }
        String responseBody = response.toString();
        System.out.println("API 호출 성공: " + responseBody);

        // JSON 파싱
        if (responseBody == null || responseBody.isEmpty()) {
          throw new ApiNotFoundException("문자 메세지 API 응답이 비어 있습니다.");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJson = mapper.readTree(responseBody);
        String code = responseJson.get("code").asText();
        String responseMessage = responseJson.get("message").asText();

        String result;
        if (code.equals("0")) {
          result = "문자 메시지 전송 성공! " + phone;
        } else {
          result = "문자 메시지 전송 실패: " + responseMessage;
        }
        connection.disconnect();
        return result;
      }
    } else {
      // API 호출이 실패한 경우의 처리
      System.out.println("API 호출 실패: " + responseCode);
      throw new ApiNotFoundException("문자 메세지 API 호출에 문제가 생겼습니다.");
    }
  }

  /**
   * 각종 메세지 발송에 대한 응답 취합
   */
  public void sendMessage(User user) throws IOException {

    // 전부 null이면 출력하지 않는다. -> 오늘 크롤링에서는 공지사항이 없었음
    List<Department> departments = departmentJpaRepository.findAll();
    int count = 0;
    List<String> validMajors = new ArrayList<>();
    for (Department department : departments) {
      if (department.getNotification().equals("") || department.getNotification().equals(null)) {
        count++;
      } else {
        validMajors.add(department.getMajor()); // 오늘 업데이트된 전공들 저장
      }
    }
    // 전부 비어있으면 종료
    if (count == departments.size()) {
      return;
    }

    // 기본 메세지 만들기
    String message = "";
    for (int i = 1; i < 5; i++) {
      Department department = departmentJpaRepository.findById(Long.valueOf(i)).get();
      message += department.getMajor() + "\n" + department.getNotification() + "\n";
    }

    String phone = user.getPhone();
    String email = user.getEmail();
    String major = user.getMajor();
    String minor = user.getMinor();

    boolean isMajorValid = false;
    boolean isMinorValid = false;

    // 오늘 공지사항 중 해당 유저와 관련이 있는 경우 찾기
    for (String validMajor : validMajors) {
      if (validMajor.equals(major)) {
        isMajorValid = true;
      }
      if (validMajor.equals(minor)) {
        isMinorValid = true;
      }
    }

    if (!isMajorValid) {
      major = "null";
    }

    if (!isMinorValid) {
      minor = "null";
    }

    // 메세지 보내기
    String responseByEmail = sendSimpleMail(message, email, major, minor);
    // String responseBySMS = sendSMS(message, phone, major, minor);

    System.out.println(responseByEmail);
    // System.out.println(responseBySMS);
  }

  /**
   * 인증 코드 전송하기
   */
  public String sendAuthenticationMessage(String phone, String code) throws IOException {

    // 요청 바디를 구성합니다.
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("callerNo", senderNum);

    // 메세지
    String text = "[CUK 알리미] 인증번호 : " + code;
    requestBody.put("message", text);

    String receiveNum =
        phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7);
    requestBody.put("receiveNos", receiveNum);

    // 요청을 보낼 URL 생성
    URL url = new URL(sendmMessageUri);

    // Open a connection through the URL
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    // 요청 방법 설정
    connection.setRequestMethod("POST");

    // 요청 헤더 설정
    connection.setRequestProperty("user-id", masterUserId);
    connection.setRequestProperty("api-key", sendmApiKey);
    connection.setRequestProperty("Content-Type", "application/json");

    // 요청 본문 설정
    connection.setDoOutput(true);
    try (OutputStream os = connection.getOutputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      mapper.writeValue(os, requestBody);
    }

    // 응답 처리
    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      // 성공적으로 API를 호출한 경우의 처리
      try (BufferedReader in = new BufferedReader(
          new InputStreamReader(connection.getInputStream()))) {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
          response.append(line);
        }
        String responseBody = response.toString();
        System.out.println("API 호출 성공: " + responseBody);

        // JSON 파싱
        if (responseBody == null || responseBody.isEmpty()) {
          throw new ApiNotFoundException("문자 메세지 API 응답이 비어 있습니다.");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJson = mapper.readTree(responseBody);
        String messageResponseCode = responseJson.get("code").asText();

        String result;
        if (messageResponseCode.equals("0")) {
          result = "성공";
        } else {
          result = "실패";
        }
        connection.disconnect();
        return result;
      }
    } else {
      // API 호출이 실패한 경우의 처리
      System.out.println("API 호출 실패: " + responseCode);
      throw new ApiNotFoundException("문자 메세지 API 호출에 문제가 생겼습니다.");
    }
  }
}


package com.hyunn.alarm.dto.response;

import lombok.Getter;

@Getter
public class UserResponse {

  private final String nickName;

  private final String email;

  private final String phoneNum;

  private final String major;

  private final String minor;

  private final String accessToken;

  public UserResponse(String nickName, String email, String phoneNum, String major, String minor,
      String accessToken) {
    this.nickName = nickName;
    this.email = email;
    this.phoneNum = phoneNum;
    this.major = major;
    this.minor = minor;
    this.accessToken = accessToken;
  }

  public static UserResponse create(String nickName, String email, String phoneNum,
      String major, String minor, String accessToken) {
    return new UserResponse(nickName, email, phoneNum, major, minor, accessToken);
  }
}

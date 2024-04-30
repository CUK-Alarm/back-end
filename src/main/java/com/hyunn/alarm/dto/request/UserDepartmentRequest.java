package com.hyunn.alarm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserDepartmentRequest {

  @NotBlank(message = "핸드폰 번호를 입력해주세요.")
  @Pattern(regexp = "^\\d{11}$", message = "핸드폰 번호는 11자리여야 합니다.")
  private String phone;

  @NotBlank(message = "전공을 입력해주세요.")
  private String major;

  @NotBlank(message = "전공을 입력해주세요.")
  private String minor;

  public UserDepartmentRequest(String phone, String major, String minor) {
    this.phone = phone;
    this.major = major;
    this.minor = minor;
  }

  public static UserDepartmentRequest create(String phone, String major, String minor) {
    return new UserDepartmentRequest(phone, major, minor);
  }
}

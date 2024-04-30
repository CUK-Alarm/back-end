package com.hyunn.alarm.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserEmailRequest {

  @NotBlank(message = "핸드폰 번호를 입력해주세요.")
  @Pattern(regexp = "^\\d{11}$", message = "핸드폰 번호는 11자리여야 합니다.")
  private String phone;

  @NotBlank(message = "이메일을 입력해주세요.")
  @Email(message = "이메일 형식을 지켜주세요.")
  private String email;


  public UserEmailRequest(String phone, String email) {
    this.phone = phone;
    this.email = email;
  }

  public static UserEmailRequest create(String phone, String email) {
    return new UserEmailRequest(phone, email);
  }
}

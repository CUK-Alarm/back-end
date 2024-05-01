package com.hyunn.alarm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserEmailRequest {

  @NotBlank(message = "핸드폰 번호를 입력해주세요.")
  private String phone;

  @NotBlank(message = "이메일을 입력해주세요.")
  private String email;

}

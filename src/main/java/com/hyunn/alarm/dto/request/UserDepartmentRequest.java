package com.hyunn.alarm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserDepartmentRequest {

  @NotBlank(message = "핸드폰 번호를 입력해주세요.")
  private String phone;

  @NotBlank(message = "제 1전공을 입력해주세요.")
  private String major;

  @NotBlank(message = "제 2전공을 입력해주세요. (전공심화라면 같은 전공을 선택해주세요.)")
  private String minor;

}

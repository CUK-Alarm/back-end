package com.hyunn.alarm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "user")
@Getter
@ToString(exclude = "userId")
@NoArgsConstructor
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  // 닉네임
  @Column(name = "nickname")
  private String nickName;

  // 이메일
  @Column(name = "email")
  private String email;

  // 전화번호
  @Column(name = "phone")
  private String phone;

  // 제 1전공
  @Column(name = "major")
  private String major;

  // 제 2전공
  @Column(name = "minor")
  private String minor;

  // 토큰
  @Column(name = "access_token")
  private String accessToken;

  // 인증 코드
  @Column(name = "code")
  private String code;

  // 역할
  @Column(name = "role")
  private String role;


  private User(String nickName, String email, String phone, String major, String minor,
      String accessToken) {
    this.nickName = nickName;
    this.email = email;
    this.phone = phone;
    this.major = major;
    this.minor = minor;
    this.accessToken = accessToken;
    this.code = null;
    this.role = "user";
  }

  public static User createUser(String nickName, String email, String phone, String major,
      String minor, String accessToken) {
    return new User(nickName, email, phone, major, minor, accessToken);
  }

  public void updateAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public void updateDepartment(String major, String minor) {
    this.major = major;
    this.minor = minor;
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updateCode(String code) {
    this.code = code;
  }
}
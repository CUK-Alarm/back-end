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
@Table(name = "department")
@Getter
@ToString(exclude = "department_id")
@NoArgsConstructor
public class Department extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "department_id")
  private Long departmentId;

  // 전공
  @Column(name = "major")
  private String major;

  // 공지사항
  @Column(name = "notification")
  private String notification;

  // Uri
  @Column(name = "uri")
  private String uri;

  public void addNotification(String notification) {
    this.notification += notification;
  }

  public void resetNotification() {
    this.notification = "";
  }
}

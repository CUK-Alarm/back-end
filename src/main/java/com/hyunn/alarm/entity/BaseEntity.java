package com.hyunn.alarm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Getter
@MappedSuperclass
public abstract class BaseEntity {

  // 수정 시간
  @LastModifiedDate
  @Column(name = "date", nullable = false)
  private LocalDateTime date;

  BaseEntity() {
    date = LocalDateTime.now();
  }

}

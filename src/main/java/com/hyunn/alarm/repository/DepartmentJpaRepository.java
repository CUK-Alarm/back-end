package com.hyunn.alarm.repository;

import com.hyunn.alarm.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentJpaRepository extends JpaRepository<Department, Long> {

  Department findByMajor(String major);

}

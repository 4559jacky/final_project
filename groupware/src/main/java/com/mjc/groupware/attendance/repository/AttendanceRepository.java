package com.mjc.groupware.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

}

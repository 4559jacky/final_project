package com.mjc.groupware.attendance.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.attendance.entity.Holiday;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    boolean existsByDate(LocalDate date);
}

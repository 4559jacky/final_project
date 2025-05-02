package com.mjc.groupware.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.member.entity.LoginLog;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
	
	
	
}

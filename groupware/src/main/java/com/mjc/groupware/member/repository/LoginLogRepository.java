package com.mjc.groupware.member.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.member.entity.LoginLog;
import com.mjc.groupware.member.entity.Member;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long>, JpaSpecificationExecutor<LoginLog> {
	
	Page<LoginLog> findByMemberOrderByLoginTimeDesc(Member member, Pageable pageable);
	Page<LoginLog> findByMemberAndLoginTimeBetweenOrderByLoginTimeDesc(Member member, LocalDateTime start, LocalDateTime end, Pageable pageable);
}

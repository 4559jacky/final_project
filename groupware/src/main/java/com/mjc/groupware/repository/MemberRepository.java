package com.mjc.groupware.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	
	Member findByMemberId(String keyword);
	
}

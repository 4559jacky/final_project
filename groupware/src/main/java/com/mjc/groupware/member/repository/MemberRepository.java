package com.mjc.groupware.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	
	Member findByMemberId(String keyword);
	List<Member> findAllByDept_DeptNo(Long deptNo);
}

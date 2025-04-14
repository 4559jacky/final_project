package com.mjc.groupware.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
	
	Member findByMemberId(String keyword);

	List<Member> findAllByDept_DeptNo(Long deptNo);

	List<Member> findByDept(Dept dept);
	
}

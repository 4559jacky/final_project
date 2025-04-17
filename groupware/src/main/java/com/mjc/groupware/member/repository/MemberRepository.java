package com.mjc.groupware.member.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
	
	Member findByMemberId(String keyword);
	
	List<Member> findAll(Specification<Member> spec);

	List<Member> findAllByDept_DeptNo(Long deptNo);
	
	@Query("SELECT m FROM Member m JOIN m.pos p WHERE m.dept.deptNo = :deptNo ORDER BY p.posOrder ASC")
    List<Member> findAllByDeptNoSortedByPosOrder(@Param("deptNo") Long deptNo);

	List<Member> findByDept(Dept dept);	
	
}

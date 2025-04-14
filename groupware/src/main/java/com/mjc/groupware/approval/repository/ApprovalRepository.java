package com.mjc.groupware.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.approval.entity.Approval;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
	
	
	// Approval 엔티티에서 member.memberNo가 일치하는 모든 데이터 찾기
	
	List<Approval> findAllByMember_MemberNo(Long member_no);

}

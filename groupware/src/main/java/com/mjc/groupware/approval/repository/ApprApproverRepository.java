package com.mjc.groupware.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.approval.entity.ApprApprover;
import com.mjc.groupware.approval.entity.Approval;

public interface ApprApproverRepository extends JpaRepository<ApprApprover, Long> {
	List<ApprApprover> findAllByMember_MemberNo(Long id);
	List<ApprApprover> findAllByApproval_ApprNo(Long id);
}

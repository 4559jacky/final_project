package com.mjc.groupware.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.approval.entity.ApprovalAttach;

public interface ApprovalAttachRepository extends JpaRepository<ApprovalAttach, Long> {
	List<ApprovalAttach> findByApproval(Approval approval);
}

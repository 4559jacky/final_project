package com.mjc.groupware.approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.approval.entity.ApprovalForm;

public interface ApprovalFormRepository extends JpaRepository<ApprovalForm, Long> {
	
}

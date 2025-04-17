package com.mjc.groupware.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.approval.entity.ApprAgreementer;

public interface ApprAgreementerRepository extends JpaRepository<ApprAgreementer, Long> {
	List<ApprAgreementer> findAllByApproval_ApprNo(Long id);
}

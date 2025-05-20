package com.mjc.groupware.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.approval.entity.ApprReferencer;

public interface ApprReferencerRepository extends JpaRepository<ApprReferencer, Long> {
	List<ApprReferencer> findAllByApproval_ApprNo(Long id);
}

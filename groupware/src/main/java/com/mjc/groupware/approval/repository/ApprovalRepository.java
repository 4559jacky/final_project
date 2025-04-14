package com.mjc.groupware.approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.approval.entity.Approval;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

}

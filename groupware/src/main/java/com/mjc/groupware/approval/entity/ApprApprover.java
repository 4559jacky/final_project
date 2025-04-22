package com.mjc.groupware.approval.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.member.entity.Member;

import groovy.transform.ToString;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="appr_approver")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprApprover {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="appr_approver_no")
	private Long apprApproverNo;
	
	@Column(name="approver_order")
	private int approverOrder;
	
	@Column(name="approver_decision_status")
	private String approverDecisionStatus;
	
	@UpdateTimestamp
	@Column(name="approver_decision_status_time")
	private LocalDateTime approverDecisionStatusTime;
	
	@Column(name="decision_reason")
	private String decisionReason;

	@ManyToOne
	@JoinColumn(name="appr_no", nullable=false)
	private Approval approval;
	
	@ManyToOne
	@JoinColumn(name="approver_no", nullable=false)
	private Member member;
	
}

package com.mjc.groupware.approval.entity;

import java.time.LocalDateTime;

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

@Entity
@Table(name="appr_agreementer")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprAgreementer {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="appr_agreementer_no")
	private Long apprAgreementerNo;
	
	@Column(name="agreementer_agree_status")
	private String agreementerAgreeStatus;
	
	@Column(name="agreementer_agree_status_time")
	private LocalDateTime agreementerAgreeStatusTime;
	
	@Column(name="agree_reason")
	private String agreeReason;

	@ManyToOne
	@JoinColumn(name="appr_no", nullable=false)
	private Approval approval;
	
	@ManyToOne
	@JoinColumn(name="agreementer_no", nullable=false)
	private Member member;
	
}

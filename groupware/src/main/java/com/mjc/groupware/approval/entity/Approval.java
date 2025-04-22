package com.mjc.groupware.approval.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.mjc.groupware.member.entity.Member;

import groovy.transform.ToString;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="approval")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Approval {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="appr_no")
	private Long apprNo;
	
	@CreationTimestamp
	@Column(name="appr_reg_date")
	private LocalDateTime apprRegDate;
	
	@Column(name="appr_res_date")
	private LocalDateTime apprResDate;
	
	@Column(name="appr_title")
	private String apprTitle;

	@Column(name="appr_text")
	private String apprText;
	
	@Column(name="appr_status")
	private String apprStatus;
	
	@Column(name="appr_order_status")
	private int apprOrderStatus;
	
	@Column(name="appr_reason")
	private String apprReason;
	
	@Column(name="start_date")
	private LocalDate startDate;
	
	@Column(name="end_date")
	private LocalDate endDate;
	
	@ManyToOne
	@JoinColumn(name="appr_sender")
	private Member member;
	
	@ManyToOne
	@JoinColumn(name="approval_type_no")
	private ApprovalForm approvalForm;
	
	@OneToMany(mappedBy="approval")
	private List<ApprApprover> approvers;
	
	@OneToMany(mappedBy="approval")
	private List<ApprAgreementer> agreementers;
	
	@OneToMany(mappedBy="approval")
	private List<ApprReferencer> referencers;
	
	
	
}

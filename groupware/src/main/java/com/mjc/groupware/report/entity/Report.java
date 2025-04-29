package com.mjc.groupware.report.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "report")
public class Report {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_no")
	private Long reportNo; // 신고 번호(ID)
	
	@Column(name = "reporter_no")
	private Long reporterNo; // 신고한 사용자
	
	@Column(name = "reported_member_no")
	private Long reportedMemberNo; // 신고 대상 사용자
	
	@Column(name = "target_type")
	private String targetType; // 신고 대상 유형
	
	@Column(name = "target_no")
	private Long targetNo; // 신고 대상(ID)
	
	@Column(name = "report_reason")
	private String reportReason; // 신고 사유
	
	@Column(name = "report_detail")
	private String reportDetail; // 신고 상세 내용
	
	@Column(name = "is_processed")
	private String isProcessed; // 처리 여부(Y/N)
	
	@Column(name = "processed_no")
	private Long processedNo; // 처리 관리자
	
	@CreationTimestamp
	@Column(name = "processed_date")
	private LocalDateTime processedDate; // 처리일
	
	@UpdateTimestamp
	@Column(name = "report_date")
	private LocalDateTime reportDate; // 신고일
	
	
}

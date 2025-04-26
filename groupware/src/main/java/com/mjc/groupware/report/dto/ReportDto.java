package com.mjc.groupware.report.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ReportDto {
	
	private Long report_no; // 신고번호(ID)
	private Long reporter_no; // 신고한 사용자
	private Long reported_member_no; // 신고 대상 사용자
	private String target_type; // 신고 대상 유형
	private Long target_no; // 신고 대상(ID)
	private String report_reason; // 신고 사유
	private String report_detail; // 신고 상세 내용
	private String is_processed; // 처리여부(Y/N)
	private Long processed_no; // 처리 관리자
	private LocalDateTime processed_date; // 처리일
	private LocalDateTime report_date; // 신고일
}

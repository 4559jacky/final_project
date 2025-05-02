package com.mjc.groupware.attendance.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.mjc.groupware.approval.entity.ApprAgreementer;
import com.mjc.groupware.approval.entity.ApprApprover;
import com.mjc.groupware.approval.entity.ApprReferencer;
import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.approval.entity.ApprovalForm;
import com.mjc.groupware.member.entity.Member;

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
import lombok.ToString;

@Entity
@Table(name = "attendance")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attendance {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="attendance_no")
	private Long attendanceNo;
}

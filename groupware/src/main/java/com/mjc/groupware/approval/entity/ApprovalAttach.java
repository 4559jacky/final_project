package com.mjc.groupware.approval.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name="approval_attach")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovalAttach {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="attach_no")
	private Long attachNo;
	
	@Column(name="ori_name")
	private String oriName;
	
	@Column(name="new_name")
	private String newName;
	
	@Column(name="attach_path")
	private String attachPath;
	
	@CreationTimestamp
	@Column(name="reg_date")
	private LocalDateTime regDate;
	
	@ManyToOne
	@JoinColumn(name="appr_no")
	private Approval approval;
	
}

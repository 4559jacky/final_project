package com.mjc.groupware.notice.entity;

import java.time.LocalDateTime;

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
import lombok.ToString;

@Entity
@Table(name="notice_attach")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Attach {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long attachNo;
	
	@Column(name="ori_name")
	private String oriName;
	
	@Column(name="new_name")
	private String newName;
	
	@Column(name="attach_path")
	private String attachPath;
	
	@ManyToOne
	@JoinColumn(name="notice_no")
	private Notice notice;
	
	@Column(name = "reg_date")
	private LocalDateTime regDate;

	@Column(name = "mod_date")
	private LocalDateTime modDate;
}

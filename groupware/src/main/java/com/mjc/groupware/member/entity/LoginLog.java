package com.mjc.groupware.member.entity;

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

@Entity
@Table(name="login_log")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LoginLog {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="login_log_no")
	private Long loginLogNo;
	
	@Column(name="login_time")
	private LocalDateTime loginTime;
	
	@Column(name="login_ip")
	private String loginIp;
	
	@Column(name="login_agent")
	private String loginAgent;
	
	@ManyToOne
	@JoinColumn(name="member_no")
	private Member member;
	
}

package com.mjc.groupware.member.entity;

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
@Table(name="member")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Member {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="member_no")
	public Long memberNo;
	
	@Column(name="member_id")
	public String memberId;
	
	@Column(name="member_pw")
	public String memberPw;
	
	@Column(name="member_name")
	private String memberName;
	
	@Column(name="member_birth")
	private String memberBirth;
	
	@Column(name="member_gender")
	private String memberGender;
	
	@Column(name="member_addr")
	private String memberAddr;
	
	@Column(name="member_phone")
	private String memberPhone;
	
	@ManyToOne
	@JoinColumn(name="role_no")
	private Role role;
	
}

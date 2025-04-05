package com.mjc.groupware.entity;

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
	
}

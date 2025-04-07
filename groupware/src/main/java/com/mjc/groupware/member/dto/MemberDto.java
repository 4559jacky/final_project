package com.mjc.groupware.member.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.entity.Role;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MemberDto {
	
	private Long member_no;
	private String member_id;
	private String member_pw;
	private String member_name;
	private String member_birth;
	private String member_gender;
	private String member_addr;
	private String member_phone;
	private Long dept_no;
	private Long pos_no;
	private Long role_no = (long) 3;
	private String status = "EMPLOYED";
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	private LocalDateTime end_date;
	
	public Member toEntity() {
		return Member.builder()
				.memberNo(this.getMember_no())
				.memberId(this.getMember_id())
				.memberPw(this.getMember_pw())
				.memberName(this.getMember_name())
				.role(Role.builder().roleNo(this.getRole_no()).build())
				.status(this.getStatus())
				.build();
	}
	
	public MemberDto toDto(Member member) {
		return MemberDto.builder()
				.member_no(member.getMemberNo())
				.member_id(member.getMemberId())
				.member_pw(member.getMemberPw())
				.member_name(member.getMemberName())
				.build();
	}
	
}

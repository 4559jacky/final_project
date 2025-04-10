package com.mjc.groupware.member.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.entity.Role;
import com.mjc.groupware.pos.entity.Pos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
	
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
	private Long pos_no;
	private Long dept_no;
	private Long role_no = (long) 3;
	private int status = 100;
	private String dept_name; // 04/09 JJI 사원 부서명 불러오기위해 추가
	private String pos_name; // 04/09 JJI 사원 직급명 불러오기위해 추가
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	private LocalDateTime end_date;
	
	public Member toEntity() {
		return Member.builder()
				.memberNo(this.getMember_no())
				.memberId(this.getMember_id())
				.memberPw(this.getMember_pw())
				.memberName(this.getMember_name())
				.memberBirth(this.getMember_birth())
				.memberGender(this.getMember_gender())
				.memberAddr(this.getMember_addr())
				.pos(Pos.builder().posNo(this.getPos_no()).build())
				.dept(Dept.builder().deptNo(this.getDept_no()).build())
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
				.dept_name(member.getDept() != null ? member.getDept().getDeptName() : null) // 04/09 JJI 사원 부서명 불러오기위해 추가
				.pos_name(member.getPos() != null ? member.getPos().getPosName() : null) // 04/09 JJI 사원 부서명 불러오기위해 추가
				.build();
	}
	
}
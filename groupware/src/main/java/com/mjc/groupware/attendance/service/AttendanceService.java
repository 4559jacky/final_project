package com.mjc.groupware.attendance.service;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mjc.groupware.attendance.dto.AnnualLeavePolicyDto;
import com.mjc.groupware.attendance.dto.AttendanceDto;
import com.mjc.groupware.attendance.dto.WorkSchedulePolicyDto;
import com.mjc.groupware.attendance.entity.AnnualLeavePolicy;
import com.mjc.groupware.attendance.entity.Attendance;
import com.mjc.groupware.attendance.entity.WorkSchedulePolicy;
import com.mjc.groupware.attendance.repository.AnnualLeavePolicyRepository;
import com.mjc.groupware.attendance.repository.AttendanceRepository;
import com.mjc.groupware.attendance.repository.WorkSchedulePolicyRepository;
import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.entity.Role;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.pos.entity.Pos;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {

	private final WorkSchedulePolicyRepository workSchedulePolicyRepository;
	private final AnnualLeavePolicyRepository annualLeavePolicyRepository;
	private final MemberRepository memberRepository;
	private final AttendanceRepository attendanceRepository;

	
	// 근태 정책 변경
	public int workTimeUpdateApi(WorkSchedulePolicyDto dto) {
		int result = 0;
		
		try {
			WorkSchedulePolicy entity = dto.toEntity();
			workSchedulePolicyRepository.save(entity);
			
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	public int annualPolicyUpdateApi(AnnualLeavePolicyDto dto) {
		int result = 0;
		
		try {
			
			AnnualLeavePolicy entity = annualLeavePolicyRepository.findByYear(dto.getYear());
			
			if(entity != null) {
				AnnualLeavePolicyDto alpDto = new AnnualLeavePolicyDto().toDto(entity);
				alpDto.setLeave_days(dto.getLeave_days());
				AnnualLeavePolicy newEntity = alpDto.toEntity();
				annualLeavePolicyRepository.save(newEntity);
			} else {
				AnnualLeavePolicy newEntity = dto.toEntity();
				annualLeavePolicyRepository.save(newEntity);
			}
			
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	public int memberAnnualUpdateApi(Long memberNo, double annualLeave) {
		
		int result = 0;
		
		try {
			Member entity = memberRepository.findById(memberNo).orElse(null);
			MemberDto memberDto = new MemberDto().toDto(entity);
			memberDto.setAnnual_leave(annualLeave);
			
			Member member = Member.builder()
								.memberNo(memberDto.getMember_no())
								.memberId(memberDto.getMember_id())
								.memberPw(memberDto.getMember_pw())
								.memberName(memberDto.getMember_name())
								.memberBirth(memberDto.getMember_birth())
								.memberGender(memberDto.getMember_gender())
								.memberAddr1(memberDto.getMember_addr1())
								.memberAddr2(memberDto.getMember_addr2())
								.memberAddr3(memberDto.getMember_addr3())
								.pos(memberDto.getPos_no() != null ? Pos.builder().posNo(memberDto.getPos_no()).build() : null)
								.dept(memberDto.getDept_no() != null ? Dept.builder().deptNo(memberDto.getDept_no()).build() : null)
								.role(Role.builder().roleNo(memberDto.getRole_no()).build())
								.status(memberDto.getStatus())
								.regDate(memberDto.getReg_date())
								.annualLeave(memberDto.getAnnual_leave())
								.signature(memberDto.getSignature())
								.build();
			
			Member saved = memberRepository.save(member);
			
			if(saved != null) {
				result = 1;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	// 연차 정책 삭제
	public int annualPolicyDeleteApi(int year) {
		int result = 0;
		try {
			AnnualLeavePolicy entity = annualLeavePolicyRepository.findByYear(year);
			
			if(entity != null) {
				annualLeavePolicyRepository.delete(entity);
			}
			
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	public Map<String, Object> saveStartTime(MemberDto member, AttendanceDto dto) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			 // 정책 조회
		    WorkSchedulePolicy wsp = workSchedulePolicyRepository.findById(1L).orElse(null);
		    LocalTime checkInTime = dto.getCheck_in();

		    if (wsp != null && checkInTime != null) {
		        if (checkInTime.isAfter(wsp.getStartTimeMax())) {
		            dto.setAttendance_status("L"); // 지각
		        } else {
		            dto.setAttendance_status("I"); // 정상 출근
		        }
		    }

		    // 사원 저장
		    dto.setMember_no(member.getMember_no());

		    // DB 저장
		    Attendance attendance = dto.toEntity();
		    Attendance entity = attendanceRepository.save(attendance);
		    
		    resultMap.put("attendance", entity);
		    resultMap.put("res_code", "200");
			resultMap.put("res_msg", "출근 정보 저장 성공");
		} catch(Exception e) {
			e.printStackTrace();
			resultMap.put("res_code", "500");
			resultMap.put("res_msg", "출근 정보 저장 실패");
		}
	   
	    return resultMap;
	}

}

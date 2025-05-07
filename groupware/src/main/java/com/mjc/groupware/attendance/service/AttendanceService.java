package com.mjc.groupware.attendance.service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.attendance.dto.AnnualLeavePolicyDto;
import com.mjc.groupware.attendance.dto.AttendPageDto;
import com.mjc.groupware.attendance.dto.AttendanceDto;
import com.mjc.groupware.attendance.dto.SearchDto;
import com.mjc.groupware.attendance.dto.WeeklyWorkDto;
import com.mjc.groupware.attendance.dto.WorkSchedulePolicyDto;
import com.mjc.groupware.attendance.entity.AnnualLeavePolicy;
import com.mjc.groupware.attendance.entity.Attendance;
import com.mjc.groupware.attendance.entity.WorkSchedulePolicy;
import com.mjc.groupware.attendance.repository.AnnualLeavePolicyRepository;
import com.mjc.groupware.attendance.repository.AttendanceRepository;
import com.mjc.groupware.attendance.repository.WorkSchedulePolicyRepository;
import com.mjc.groupware.attendance.specification.AttendanceSpecification;
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

	// 근무 출근 저장
	public Map<String, Object> saveStartTime(MemberDto member, AttendanceDto dto) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			 // 정책 조회
		    WorkSchedulePolicy wsp = workSchedulePolicyRepository.findById(1L).orElse(null);
		    LocalTime checkInTime = dto.getCheck_in();
		    
		    if (wsp != null && checkInTime != null) {
		        if (checkInTime.isAfter(wsp.getStartTimeMax())) {
		            dto.setLate_yn("Y"); // 지각여부
		        } else {
		            dto.setLate_yn("N"); // 정상 출근
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

	// 퇴근 시간 저장
	public Map<String, Object> saveEndTime(MemberDto member, AttendanceDto dto) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		System.out.println(">>> memberNo: " + member.getMember_no());
		System.out.println(">>> attendDate: " + dto.getAttend_date());
		try {
			// 오늘의 근무 정보 가져오기
			Attendance attendance = attendanceRepository.findByMember_MemberNoAndAttendDate(member.getMember_no(), dto.getAttend_date());
			AttendanceDto oldAttendanceDto = new AttendanceDto().toDto(attendance);
			oldAttendanceDto.setCheck_out(dto.getCheck_out());
			oldAttendanceDto.setEarly_leave_yn(dto.getEarly_leave_yn());
			
			// 하루 일한 시간 구하기
			LocalTime checkIn = oldAttendanceDto.getCheck_in();
			LocalTime checkOut = dto.getCheck_out();
			Duration duration = Duration.between(checkIn, checkOut);
			LocalTime workingTime = LocalTime.ofSecondOfDay(duration.getSeconds());
			oldAttendanceDto.setWorking_time(workingTime);
			
			Attendance updated = oldAttendanceDto.toEntity();
	        attendanceRepository.save(updated);
	        
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "퇴근 시간이 저장되었습니다.");
	        resultMap.put("attendance", oldAttendanceDto);
	        
		} catch(Exception e) {
			e.printStackTrace();
			resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "퇴근 저장 실패");
		}
		
		
		return resultMap;
	}


	public List<WeeklyWorkDto> getWeeklyWorkTime(String startDate, String endDate, MemberDto memberDto) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    LocalDate start = LocalDate.parse(startDate, formatter);
	    LocalDate end = LocalDate.parse(endDate, formatter);

	    // MemberEntity 가져오기
	    Member memberEntity = memberRepository.findById(memberDto.getMember_no()).orElse(null);
	    if (memberEntity == null) return Collections.emptyList();

	    List<Attendance> attendanceList = attendanceRepository.findByAttendDateBetweenAndMember(start, end, memberEntity);

	    List<WeeklyWorkDto> result = new ArrayList<>();

	    for (Attendance a : attendanceList) {
	        double hours = 0.0;
	        if (a.getWorkingTime() != null) {
	            hours = convertTimeToHours(a.getWorkingTime());
	        }

	        WeeklyWorkDto dto = WeeklyWorkDto.builder()
	            .attendDate(a.getAttendDate().toString())
	            .workingHours(hours)
	            .build();

	        result.add(dto);
	    }

	    return result;
	}
	
	// 일한시간 시간 소수점 한자리까지 반올림해서 실수로 계산
	private double convertTimeToHours(LocalTime time) {
	    int hour = time.getHour();
	    int minute = time.getMinute();
	    int second = time.getSecond();

	    double totalMinutes = hour * 60 + minute + second / 60.0;
	    double hours = totalMinutes / 60.0;

	    // 소수점 한자리로 반올림
	    return Math.round(hours * 10) / 10.0;
	}

	// 사원 모든 근무이력 데이터 가져오기
	public List<Attendance> selectAttendanceAll(Member member) {
		List<Attendance> attendanceList = null;
		attendanceList = attendanceRepository.findAll();
		return attendanceList;
	}

	// 근무 이력 검색 필터 페이징
	public Page<Attendance> selectAttendanceAllByFilter(Member member, SearchDto searchDto, AttendPageDto pageDto) {
		Pageable pageable = PageRequest.of(pageDto.getNowPage() -1, pageDto.getNumPerPage(),
				Sort.by("attendDate").descending());
		if(searchDto.getOrder_type() == 2) {
			pageable = PageRequest.of(pageDto.getNowPage() -1, pageDto.getNumPerPage(),
					Sort.by("attendDate").ascending());
		}
		
		Specification<Attendance> spec = (root, query, criteriaBuilder) -> null;
		spec = spec.and(AttendanceSpecification.attendanceLateYnContains(searchDto.getCheckInStatus()))
				.and(AttendanceSpecification.attendanceMemberContains(member.getMemberNo()))
				.and(AttendanceSpecification.attendanceEarlyLeaveYnContains(searchDto.getCheckOutStatus()));
				
		Page<Attendance> list = attendanceRepository.findAll(spec, pageable);
		return list;
	}

}

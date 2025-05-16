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
import com.mjc.groupware.accommodationReservation.controller.AccommodationAdminController;
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
	public Map<String,Object> workTimeUpdateApi(WorkSchedulePolicyDto dto) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		try {
			WorkSchedulePolicy entity = dto.toEntity();
			WorkSchedulePolicy saved = workSchedulePolicyRepository.save(entity);
			resultMap.put("workPolicy", saved);
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "근태 정책이 변경되었습니다.");

		} catch(Exception e) {
			e.printStackTrace();
			resultMap.put("res_code", "500");
			resultMap.put("res_msg", "근태 정책 변경에 실패하였습니다.");
		}
		return resultMap;
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
	    Map<String, Object> resultMap = new HashMap<>();
	    try {
	        WorkSchedulePolicy wsp = workSchedulePolicyRepository.findById(1L).orElse(null);
	        LocalTime checkInTime = dto.getCheck_in();
	        String planTitle = dto.getPlan_title(); // 프론트에서 넘긴 값

	        if (wsp != null && checkInTime != null) {
	            LocalTime startMax = wsp.getStartTimeMax();

	            // 오전반차일 경우 출근 기준 시간을 4시간 늦춤
	            if (planTitle != null && planTitle.contains("오전반차")) {
	                startMax = startMax.plusHours(4);
	            }

	            if (checkInTime.isAfter(startMax)) {
	                dto.setLate_yn("Y");
	            } else {
	                dto.setLate_yn("N");
	            }
	        }

	        dto.setMember_no(member.getMember_no());

	        Attendance attendance = dto.toEntity();
	        Attendance entity = attendanceRepository.save(attendance);

	        resultMap.put("attendance", entity);
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "출근 정보 저장 성공");
	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "출근 정보 저장 실패");
	    }

	    return resultMap;
	}

	// 퇴근 시간 저장
	public Map<String, Object> saveEndTime(MemberDto member, AttendanceDto dto) {
	    Map<String, Object> resultMap = new HashMap<>();
	    System.out.println(">>> memberNo: " + member.getMember_no());
	    System.out.println(">>> attendDate: " + dto.getAttend_date());
	    try {
	        Attendance attendance = attendanceRepository.findByMember_MemberNoAndAttendDate(
	            member.getMember_no(), dto.getAttend_date());
	        AttendanceDto oldAttendanceDto = new AttendanceDto().toDto(attendance);

	        oldAttendanceDto.setCheck_out(dto.getCheck_out());

	        // 정책 조회
	        WorkSchedulePolicy wsp = workSchedulePolicyRepository.findById(1L).orElse(null);
	        double workDuration = wsp != null ? wsp.getWorkDuration() : 8.5;

	        // 휴가 타입 가져오기
	        String planTitle = dto.getPlan_title();
	        boolean isMorningLeave = planTitle != null && planTitle.contains("오전반차");
	        boolean isAfternoonLeave = planTitle != null && planTitle.contains("오후반차");

	        // 출근 / 퇴근 시간
	        LocalTime checkIn = oldAttendanceDto.getCheck_in();
	        LocalTime checkOut = dto.getCheck_out();
	        Duration duration = Duration.between(checkIn, checkOut);

	        // 🔥 총 근무 시간 계산
	        long totalSeconds = duration.getSeconds();

	        // 🔥 9시간 이상이면 자동 휴게시간 1시간 감산
	        if (totalSeconds >= 9 * 60 * 60) {
	            totalSeconds -= 60 * 60;
	        }

	        // 🔥 실근무시간 저장
	        LocalTime workingTime = LocalTime.ofSecondOfDay(totalSeconds);
	        oldAttendanceDto.setWorking_time(workingTime);

	        // 조퇴 여부 판단
	        long workedMinutes = totalSeconds / 60;
	        long requiredMinutes;

	        if (isMorningLeave || isAfternoonLeave) {
	            requiredMinutes = 4 * 60;
	        } else {
	            requiredMinutes = (long) (workDuration * 60);
	        }

	        if (workedMinutes < requiredMinutes) {
	            oldAttendanceDto.setEarly_leave_yn("Y");
	        } else {
	            oldAttendanceDto.setEarly_leave_yn("N");
	        }

	        Attendance updated = oldAttendanceDto.toEntity();
	        attendanceRepository.save(updated);

	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "퇴근 정보 저장 성공");
	        resultMap.put("attendance", oldAttendanceDto);

	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "퇴근 정보 저장 실패");
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
		spec = spec.and(AttendanceSpecification.attendanceLateYnContains(searchDto.getCheck_in_status()))
				.and(AttendanceSpecification.attendanceMemberContains(member.getMemberNo()))
				.and(AttendanceSpecification.attendanceEarlyLeaveYnContains(searchDto.getCheck_out_status()))
				.and(AttendanceSpecification.attendDateAfter(searchDto.getStart_date()))
				.and(AttendanceSpecification.attendDateBefore(searchDto.getEnd_date()));
		Page<Attendance> list = attendanceRepository.findAll(spec, pageable);
		return list;
	}

	// 관리자 - 회원 근태 정보 수정
	public int memberAttendStatusUpdateApi(Member member, AttendanceDto dto) {
		
		int result = 0;
		
		try {
			System.out.println("test : "+dto.getAttend_date());
			Attendance entity = attendanceRepository.findByMember_MemberNoAndAttendDate(
		            member.getMemberNo(), dto.getAttend_date());
			
			AttendanceDto beforeDto = new AttendanceDto().toDto(entity);
			beforeDto.setCheck_in(dto.getCheck_in());
			beforeDto.setCheck_out(dto.getCheck_out());
			beforeDto.setLate_yn(dto.getLate_yn());
			beforeDto.setEarly_leave_yn(dto.getEarly_leave_yn());
			
			// 출근 / 퇴근 시간
	        LocalTime checkIn = dto.getCheck_in();
	        LocalTime checkOut = dto.getCheck_out();
	        Duration duration = Duration.between(checkIn, checkOut);

	        // 총 근무 시간 계산
	        long totalSeconds = duration.getSeconds();

	        // 9시간 이상이면 자동 휴게시간 1시간 감산
	        if (totalSeconds >= 9 * 60 * 60) {
	            totalSeconds -= 60 * 60;
	        }

	        // 실근무시간 저장
	        LocalTime workingTime = LocalTime.ofSecondOfDay(totalSeconds);
	        beforeDto.setWorking_time(workingTime);
	        
	        
	        Attendance attendance = beforeDto.toEntity();
	        attendanceRepository.save(attendance);
	        
	        result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}

package com.mjc.groupware.common.scheduler;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.attendance.dto.LeaveGrantHistory;
import com.mjc.groupware.attendance.entity.AnnualLeavePolicy;
import com.mjc.groupware.attendance.entity.Attendance;
import com.mjc.groupware.attendance.entity.Holiday;
import com.mjc.groupware.attendance.repository.AnnualLeavePolicyRepository;
import com.mjc.groupware.attendance.repository.AttendanceRepository;
import com.mjc.groupware.attendance.repository.HolidayRepository;
import com.mjc.groupware.attendance.repository.LeaveGrantHistoryRepository;
import com.mjc.groupware.attendance.service.HolidayService;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.plan.entity.Plan;
import com.mjc.groupware.plan.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegularEventScheduler {
	private final MemberRepository memberRepository;
	private final AnnualLeavePolicyRepository annualLeavePolicyRepository;
	private final HolidayService holidayService;
	private final HolidayRepository holidayRepository;
	private final AttendanceRepository attendanceRepository;
	private final LeaveGrantHistoryRepository leaveGrantHistoryRepository;
	private final PlanRepository planRepository;
	
	@Scheduled(cron = "0 0 0 * * *")
	@Transactional(rollbackFor = Exception.class)
	public void giveAnnualLeave() {
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
		List<Member> memberList = memberRepository.findAll();
		
		int givenCount = 0;
		
		for(Member m : memberList) {
			if(m.getRegDate() == null) continue;
			
			LocalDate regDate = m.getRegDate();
			int years = Period.between(regDate, today).getYears();
			
			// 오늘이 입사 N주기인지 확인
			if(!regDate.plusYears(years).isEqual(today)) continue;
			
			int serviceYears = years;
			
			AnnualLeavePolicy policy = annualLeavePolicyRepository.findByYear(serviceYears);
			
			if(policy == null) continue;
			
			double addLeave = policy.getLeaveDays();
			m.updateAnnualLeave(addLeave);
			memberRepository.save(m);
			
			givenCount++;
		}
		System.out.println("연차 지급 완료 : " + givenCount + "명");
	}
	
	// 1월1일에 한번 그 해의 공휴일 데이터를 가져옴
	@Scheduled(cron = "0 0 0 1 1 *")
	public void fetchHolidayOnNewYear() {
	    int nextYear = LocalDate.now(ZoneId.of("Asia/Seoul")).getYear();
	    holidayRepository.deleteAll();
	    try {
	        holidayService.fetchAndSaveHolidays(nextYear);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	// 매일 연차인 사원 근태 이력 저장(날짜만)
	@Scheduled(cron = "0 0 8 * * ?")
	public void annualMemberAttendAdd() {
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
		
		List<Plan> planList = planRepository.findAllByStartDateAndPlanType(today, "휴가");
		for(Plan p : planList) {
			if(p.getPlanTitle().contains("[연차]")) {
				Member member = memberRepository.findById(p.getMember().getMemberNo()).orElse(null);
				
				Attendance attendance = new Attendance();
				attendance.annualMemberAttendance(today, member);
				attendanceRepository.save(attendance);
			}
		}
		
	}
	
	@Scheduled(cron = "0 0 1 * * ?")
	@Transactional(rollbackFor = Exception.class)
	public void grantMonthlyLeave() {
	    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
	    List<Member> allMembers = memberRepository.findAll();
	    List<Holiday> holidays = holidayRepository.findByDateBetween(today.minusMonths(1), today);

	    System.out.println("스케줄러 동작중");
	    System.out.println("오늘 날짜 : " + today);

	    for (Member member : allMembers) {
	        LocalDate regDate = member.getRegDate();
	        long totalDays = ChronoUnit.DAYS.between(regDate, today);

	        if (totalDays < 30 || totalDays >= 365) continue;

	        long completedMonthCount = totalDays / 30;

	        for (int i = 1; i <= completedMonthCount; i++) {
	            LocalDate start = regDate.plusDays((i - 1) * 30);
	            LocalDate end = regDate.plusDays(i * 30 - 1);

	            boolean alreadyGranted = leaveGrantHistoryRepository.existsByMemberNoAndPeriod(
	                member.getMemberNo(), start, end);
	            if (alreadyGranted) continue;

	            List<LocalDate> workingDates = getWorkingDates(start, end, holidays);
	            List<LocalDate> attendedDates = attendanceRepository.findAttendedDates(
	                member.getMemberNo(), start, end);

	            boolean isPerfect = workingDates.stream().allMatch(attendedDates::contains);

	            if (isPerfect) {
	                member.updateAnnualLeave(1.0);
	                memberRepository.save(member);

	                LeaveGrantHistory lgh = LeaveGrantHistory.create(member, start, end);
	                leaveGrantHistoryRepository.save(lgh);

	                System.out.println("✅ [" + member.getMemberName() + "] 연차 1일 지급 완료 (" + start + " ~ " + end + ")");
	            }
	        }
	    }
	}
	
	
	// 주말 및 공휴일 계산
//	private long calculateWorkingDays(LocalDate start, LocalDate end, List<Holiday> holidays) {
//	    Set<LocalDate> holidaySet = holidays.stream()
//	        .map(Holiday::getDate)
//	        .collect(Collectors.toSet());
//
//	    long count = 0;
//	    for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
//	        if (date.getDayOfWeek().getValue() >= 6) continue; // 주말 제외
//	        if (holidaySet.contains(date)) continue; // 공휴일 제외
//	        count++;
//	    }
//	    return count;
//	}
	
	private List<LocalDate> getWorkingDates(LocalDate start, LocalDate end, List<Holiday> holidays) {
	    Set<LocalDate> holidaySet = holidays.stream()
	        .map(Holiday::getDate)
	        .collect(Collectors.toSet());

	    List<LocalDate> workingDates = new ArrayList<>();
	    for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
	        if (date.getDayOfWeek().getValue() >= 6) continue; // 토/일 제외
	        if (holidaySet.contains(date)) continue;           // 공휴일 제외
	        workingDates.add(date);
	    }
	    return workingDates;
	}
}

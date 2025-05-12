package com.mjc.groupware.common.scheduler;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.attendance.entity.AnnualLeavePolicy;
import com.mjc.groupware.attendance.repository.AnnualLeavePolicyRepository;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegularEventScheduler {
	private final MemberRepository memberRepository;
	private final AnnualLeavePolicyRepository annualLeavePolicyRepository;
	
	@Scheduled(cron = "0 0 0 * * *")
	@Transactional(rollbackFor = Exception.class)
	public void giveAnnualLeave() {
		LocalDate today = LocalDate.now();
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
}

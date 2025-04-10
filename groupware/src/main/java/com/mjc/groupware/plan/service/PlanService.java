package com.mjc.groupware.plan.service;

import org.springframework.stereotype.Service;

import com.mjc.groupware.plan.dto.PlanDto;
import com.mjc.groupware.plan.entity.Plan;
import com.mjc.groupware.plan.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {

	private final PlanRepository planRepository;

	public Plan savePlan(PlanDto dto) {
	   Plan entity = dto.toEntity();
	   Plan result = planRepository.save(entity);
	   return result;
    }

}

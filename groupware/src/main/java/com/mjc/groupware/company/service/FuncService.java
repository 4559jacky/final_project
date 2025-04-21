package com.mjc.groupware.company.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.company.dto.FuncDto;
import com.mjc.groupware.company.entity.Func;
import com.mjc.groupware.company.repository.FuncRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuncService {
	
	private final FuncRepository repository;
	
	@Transactional(rollbackFor = Exception.class)
	public FuncDto updateAvailableFunc(FuncDto dto) {
		FuncDto result = null;
		Func target = repository.findById(dto.getFunc_no()).orElseThrow(() -> new IllegalArgumentException("해당 기능을 조회할 수 없습니다."));
		
		if(target != null) {
			target.changeFuncStatus(dto.getFunc_status());
			result = FuncDto.builder()
					.func_no(target.getFuncNo())
					.func_name(target.getFuncName())
					.func_url(target.getFuncUrl())
					.func_status(target.getFuncStatus())
					.parent_func_no(target.getParentFunc() != null ? target.getParentFunc().getFuncNo() : null)
					.reg_date(target.getRegDate())
					.mod_date(target.getModDate())
					.build();
		}
		
		return result;
	}
	
}

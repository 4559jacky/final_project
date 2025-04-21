package com.mjc.groupware.company.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.company.dto.FuncDto;
import com.mjc.groupware.company.entity.Func;
import com.mjc.groupware.company.entity.FuncMapping;
import com.mjc.groupware.company.repository.FuncMappingRepository;
import com.mjc.groupware.company.repository.FuncRepository;
import com.mjc.groupware.member.dto.RoleDto;
import com.mjc.groupware.member.entity.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuncService {
	
	private final FuncRepository repository;
	private final FuncMappingRepository funcMappingRepository;
	
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
	
	public List<FuncDto> selectSubFuncByFuncNoWithRoles(Long funcNo) {
		List<Func> result = repository.findSubFuncByFuncNo(funcNo);
		
		List<FuncDto> resultList = new ArrayList<>();
		
		for(Func func : result) {
			List<FuncMapping> mappings = funcMappingRepository.findByFuncFuncNo(func.getFuncNo());
			List<RoleDto> roleList = new ArrayList<>();
			
			for(FuncMapping mapping : mappings) {
				Role role = mapping.getRole();
				
				RoleDto roleDto = RoleDto.builder()
						.role_no(role.getRoleNo())
						.role_name(role.getRoleName())
						.role_nickname(role.getRoleNickname())
						.build();
				
				roleList.add(roleDto);
			}
			
			FuncDto funcDto = FuncDto.builder()
					.func_no(func.getFuncNo())
					.func_name(func.getFuncName())
					.func_url(func.getFuncUrl())
					.func_status(func.getFuncStatus())
					.parent_func_no(func.getParentFunc() != null ? func.getParentFunc().getFuncNo() : null)
					.reg_date(func.getRegDate())
					.mod_date(func.getModDate())
					.accessibleRoles(roleList)
					.build();
			
			resultList.add(funcDto);
		}
		
		return resultList;
	}
	
	public FuncDto selectFuncByFuncNoWithRoles(Long funcNo) {
		Func result = repository.findById(funcNo).orElse(null);
		
		if(result == null) {
			return null;
		}
		
		List<FuncMapping> mappings = funcMappingRepository.findByFuncFuncNo(funcNo);
		List<RoleDto> roleList = new ArrayList<>();
		
		for (FuncMapping mapping : mappings) {
			Role role = mapping.getRole();
			
			RoleDto roleDto = RoleDto.builder()
					.role_no(role.getRoleNo())
					.role_name(role.getRoleName())
					.role_nickname(role.getRoleNickname())
					.build();
			
			roleList.add(roleDto);
		}
		
		FuncDto funcDto = FuncDto.builder()
				.func_no(result.getFuncNo())
				.func_name(result.getFuncName())
				.func_url(result.getFuncUrl())
				.func_status(result.getFuncStatus())
				.parent_func_no(result.getParentFunc() != null ? result.getParentFunc().getFuncNo() : null)
				.reg_date(result.getRegDate())
				.mod_date(result.getModDate())
				.accessibleRoles(roleList)
				.build();
		
		return funcDto;
	}
	
}

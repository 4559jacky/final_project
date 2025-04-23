package com.mjc.groupware.company.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.company.dto.FuncDto;
import com.mjc.groupware.company.dto.FuncMappingRequestDto;
import com.mjc.groupware.company.entity.Func;
import com.mjc.groupware.company.entity.FuncMapping;
import com.mjc.groupware.company.repository.FuncMappingRepository;
import com.mjc.groupware.company.repository.FuncRepository;
import com.mjc.groupware.member.dto.RoleDto;
import com.mjc.groupware.member.entity.Role;
import com.mjc.groupware.member.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FuncService {
	
	private final FuncRepository repository;
	private final FuncMappingRepository funcMappingRepository;
	private final RoleRepository roleRepository;
	
	@Transactional(rollbackFor = Exception.class)
	public FuncDto updateAvailableFunc(FuncDto dto) {
		FuncDto result = null;
		Func target = repository.findById(dto.getFunc_no()).orElseThrow(() -> new IllegalArgumentException("해당 기능을 조회할 수 없습니다."));
		
		if(target != null) {
			target.changeFuncStatus(dto.getFunc_status());
			result = FuncDto.builder()
					.func_no(target.getFuncNo())
					.func_name(target.getFuncName())
					.func_code(target.getFuncCode())
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
					.func_code(func.getFuncCode())
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
				.func_code(result.getFuncCode())
				.func_status(result.getFuncStatus())
				.parent_func_no(result.getParentFunc() != null ? result.getParentFunc().getFuncNo() : null)
				.reg_date(result.getRegDate())
				.mod_date(result.getModDate())
				.accessibleRoles(roleList)
				.build();
		
		return funcDto;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void updateFuncMapping(FuncMappingRequestDto dto) {
		Long funcNo = dto.getFunc_no();
		Long roleNo = dto.getRole_no();
		boolean isChecked = dto.is_checked();
		
		// validation 체크
		Func func = repository.findById(funcNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기능입니다: " + funcNo));

		Role role = roleRepository.findById(roleNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역할입니다: " + roleNo));
		
		// func, role 기반으로 검색해서 뭔가가 존재하면? -> 수정
		FuncMapping exists = funcMappingRepository.findByFuncAndRole(func, role);
		
		if(isChecked) {
			// 추가를 원하는 경우
			if(exists == null) {
				funcMappingRepository.save(FuncMapping.builder()
						.func(func)
						.role(role)
						.build());
			} else {
				funcMappingRepository.save(FuncMapping.builder()
						.funcMappingNo(exists.getFuncMappingNo())
						.func(func)
						.role(role)
						.build());
			}
		} else {
			// 제거를 원하는 경우
			if(exists != null) {
				funcMappingRepository.delete(exists);
			}
		}
	}
	
}

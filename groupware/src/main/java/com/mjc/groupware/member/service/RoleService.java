package com.mjc.groupware.member.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.mjc.groupware.member.dto.RoleDto;
import com.mjc.groupware.member.entity.Role;
import com.mjc.groupware.member.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
	
	private final RoleRepository repository;
	
	public List<Role> selectRoleAll() {
		return repository.findAll();
	}
	
	public RoleDto createRole(RoleDto dto) {
		try {
			dto.setRole_name("ROLE_"+dto.getRole_name().toUpperCase());
			
			Role result = repository.save(Role.builder()
					.roleNickname(dto.getRole_nickname())
					.roleName(dto.getRole_name())
					.build());
			
			return RoleDto.builder()
					.role_no(result.getRoleNo())
					.role_nickname(result.getRoleNickname())
					.role_name(result.getRoleName().replace("ROLE_", ""))
					.build();
		} catch (DataIntegrityViolationException e) {
	        throw new IllegalArgumentException("이미 사용 중인 권한 코드입니다.");
	    }
	}
	
}

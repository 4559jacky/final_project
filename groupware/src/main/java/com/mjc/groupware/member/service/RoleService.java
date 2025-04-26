package com.mjc.groupware.member.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.company.dto.RoleDetailResponseDto;
import com.mjc.groupware.company.entity.Func;
import com.mjc.groupware.company.repository.FuncRepository;
import com.mjc.groupware.member.dto.RoleDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.entity.Role;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.member.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
	
	private Logger logger = LoggerFactory.getLogger(RoleService.class);
	
	private final RoleRepository repository;
	private final MemberRepository memberRepository;
	private final FuncRepository funcRepository;
	
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
	
	public RoleDto updateRole(RoleDto dto) {
		try {
			dto.setRole_name("ROLE_"+dto.getRole_name().toUpperCase());
			
			Role result = repository.save(Role.builder()
					.roleNo(dto.getRole_no())
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
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteRole(RoleDto dto) {
		try {
			Long deleteRoleNo = dto.getRole_no();
			
			List<Member> target = memberRepository.findByRole_RoleNo(deleteRoleNo);
			
			Role defaultRole = repository.getReferenceById(2L);	// 다시 조회해서 넣어주는 식으로 구성할 것 - JPA :: 영속성 예외 방지
			
			for (Member member : target) {
	            member.changeRoleNo(defaultRole);
	        }
			
			repository.deleteById(deleteRoleNo);
		} catch(Exception e) {
			logger.error("권한 삭제 중 오류 발생", e);
	        throw e;
		}
	}
	
	public List<RoleDto> selectRoleDtoAll() {
		try {
			List<Role> roleList = repository.findAll();
			
			List<RoleDto> roleDtoList = new ArrayList<>();
			
			for (Role role : roleList) {
	            RoleDto dto = RoleDto.builder()
	                    .role_no(role.getRoleNo())
	                    .role_name(role.getRoleName())
	                    .role_nickname(role.getRoleNickname())
	                    .build();
	            roleDtoList.add(dto);
	        }
			
			return roleDtoList;
		} catch(Exception e) {
			logger.error("역할 목록 조회 중 오류 발생", e);
			return Collections.emptyList();
		}
	}
	
}

package com.mjc.groupware.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
	
}

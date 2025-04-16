package com.mjc.groupware.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.member.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
	
	
}

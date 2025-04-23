package com.mjc.groupware.company.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.company.entity.Func;
import com.mjc.groupware.company.entity.FuncMapping;
import com.mjc.groupware.member.entity.Role;

public interface FuncMappingRepository extends JpaRepository<FuncMapping, Long> {
	
	List<FuncMapping> findByFuncFuncNo(Long funcNo);
	
	FuncMapping findByFuncAndRole(Func func, Role role);
	
    void deleteByFuncAndRole(Func func, Role role);
	
    List<FuncMapping> findByRole_RoleNo(Long roleNo);
}

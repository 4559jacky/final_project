package com.mjc.groupware.company.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.company.entity.FuncMapping;

public interface FuncMappingRepository extends JpaRepository<FuncMapping, Long> {
	
	List<FuncMapping> findByFuncFuncNo(Long funcNo);
	
}

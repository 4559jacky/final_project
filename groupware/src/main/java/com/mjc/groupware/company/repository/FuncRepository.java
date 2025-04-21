package com.mjc.groupware.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.company.entity.Func;

public interface FuncRepository extends JpaRepository<Func, Long>, JpaSpecificationExecutor<Func> {
	
	
	
}

package com.mjc.groupware.company.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.company.entity.Func;

public interface FuncRepository extends JpaRepository<Func, Long>, JpaSpecificationExecutor<Func> {
	
	@Query("SELECT f FROM Func f WHERE f.parentFunc.funcNo = :funcNo")
    List<Func> findSubFuncByFuncNo(@Param("funcNo") Long funcNo);
	
}

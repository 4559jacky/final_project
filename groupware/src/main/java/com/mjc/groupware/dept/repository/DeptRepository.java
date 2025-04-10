package com.mjc.groupware.dept.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.dept.entity.Dept;

public interface DeptRepository extends JpaRepository<Dept, Long> {
	
	boolean existsByDeptName(String deptName);
	
}

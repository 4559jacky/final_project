package com.mjc.groupware.dept.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.dept.entity.Dept;

public interface DeptRepository extends JpaRepository<Dept, Long> {
	
	boolean existsByDeptName(String deptName);
	Dept findByDeptName(String deptName);
	List<Dept> findByParentDept(Dept parentDept);
	
	List<Dept> findByDeptStatusNotOrderByDeptNameAsc(Integer deptStatus);
	
	// 상태값이 3이 아닌 객체들끼리는 이름이 겹쳐서는 안 되기에 중복되는 이름이 있나 확인하는 메소드 ... 이정도면 spec 쓰는게 나을지도 모르겠음
	boolean existsByDeptNameAndDeptStatusNot(String deptName, Integer excludedStatus);
	List<Dept> findByDeptNameAndDeptStatusNot(String deptName, int excludedStatus);
	
	List<Dept> findAllByOrderByDeptNameAsc();
	
}

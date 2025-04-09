package com.mjc.groupware.dept.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mjc.groupware.dept.dto.DeptTreeDto;
import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.dept.repository.DeptRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeptTreeServiceImpl implements DeptTreeService {
	
	private final DeptRepository deptRepository;
	
	@Override
	public List<DeptTreeDto> getDeptTree() {
		List<Dept> deptList = deptRepository.findAll();
		List<DeptTreeDto> treeList = new ArrayList<>();
		
		for (Dept dept : deptList) {
            treeList.add(new DeptTreeDto(
                String.valueOf(dept.getDeptNo()),
                dept.getParentDept() != null ? String.valueOf(dept.getParentDept().getDeptNo()) : "#",
                dept.getDeptName(),
                "dept"
            ));
        }
		
		return treeList;
	}
	
}

package com.mjc.groupware.dept.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			// 삭제 상태인 경우 jsTree에서 제외
	        if (dept.getDeptStatus() == 3) {
	            continue;
	        }
			
	        // 비활성 상태인 경우 jsTree에 취소선 부여
			Map<String, String> aAttr = new HashMap<>();
			
			if (dept.getDeptStatus() == 2) {
		        aAttr.put("class", "text-decoration-line-through text-muted");
		    }
			
			// 활성 상태인 정상적인 경우
            treeList.add(new DeptTreeDto(
                String.valueOf(dept.getDeptNo()),
                dept.getParentDept() != null ? String.valueOf(dept.getParentDept().getDeptNo()) : "#",
                dept.getDeptName(),
                "dept",
                aAttr
            ));
        }
		
		treeList.sort(Comparator.comparing(DeptTreeDto::getText, String.CASE_INSENSITIVE_ORDER));
		
		return treeList;
	}
	
}
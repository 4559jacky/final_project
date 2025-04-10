package com.mjc.groupware.dept.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mjc.groupware.dept.dto.DeptDto;
import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.dept.repository.DeptRepository;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeptService {
	
	private final DeptRepository repository;
	private final MemberRepository memberRepository;
	
	public List<Dept> selectDeptAll() {
		return repository.findAll();
	}
	
	public Dept createDept(DeptDto dto) {
		if(repository.existsByDeptName(dto.getDept_name())) {
		    throw new IllegalArgumentException("이미 존재하는 부서명입니다.");
		}
		
		Member member = null;
		if(dto.getMember_no() != null) {
			member = memberRepository.findById(dto.getMember_no())
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원입니다."));
		}

		Dept parentDept = null;
		if(dto.getParent_dept_no() != null) {
			parentDept = repository.findById(dto.getParent_dept_no())
					.orElseThrow(() -> new IllegalArgumentException("상위 부서가 존재하지 않습니다."));
			
			if (parentDept.getDeptNo().equals(dto.getDept_no())) {
				throw new IllegalArgumentException("자기 자신을 상위 부서로 설정할 수 없습니다.");
			}
		}

		Dept dept = dto.toEntity(member, parentDept);
		
		return repository.save(dept);
	}
	
	public Dept selectDeptByDeptNo(Long deptNo) {
		Dept result = repository.findById(deptNo).orElse(null);
		
		if(result == null) {
			throw new IllegalArgumentException("존재하지 않는 부서입니다.");
		}
		
		return result;
	}
	
	public Dept updateDept(DeptDto dto) {
		Dept param = repository.findById(dto.getDept_no()).orElse(null);
		
		if(param == null) {
			throw new IllegalArgumentException("존재하지 않는 부서입니다.");
		}
		
		if(repository.findByDeptName(dto.getDept_name()) != null && repository.findByDeptName(dto.getDept_name()).getDeptNo() != param.getDeptNo()) {
		    throw new IllegalArgumentException("이미 존재하는 부서명입니다.");
		}
		
		Dept result = repository.save(Dept.builder()
				.deptNo(param.getDeptNo())
				.deptName(dto.getDept_name())
				.member(Member.builder().memberNo(dto.getMember_no()).build())
				.parentDept(Dept.builder().deptNo(dto.getParent_dept_no()).build())
				.deptPhone(dto.getDept_phone())
				.deptLocation(dto.getDept_location())
				.deptStatus(dto.getDept_status())
				.build());
		
		return result;
				
	}
	
}

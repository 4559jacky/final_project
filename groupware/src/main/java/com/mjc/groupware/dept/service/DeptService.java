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
	
	public List<Dept> selectByDeptStatusNot(int deptStatus) {
		return repository.findByDeptStatusNotOrderByDeptNameAsc(deptStatus);
	}
	
	public List<Dept> SelectDeptAllOrderByDeptNameAsc() {
		return repository.findAllByOrderByDeptNameAsc();
	}
	
	public Dept createDept(DeptDto dto) {
		if(repository.existsByDeptNameAndDeptStatusNot(dto.getDept_name(), 3)) {
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
		
		// 상태가 삭제(3)가 아닌 부서 중 이름이 중복되는 경우 예외 발생시켜서 활성, 비활성 상태의 부서끼리는 이름이 겹칠 수 없도록 함
	    List<Dept> duples = repository.findByDeptNameAndDeptStatusNot(dto.getDept_name(), 3);
	    for(Dept dept : duples) {
	        if(!dept.getDeptNo().equals(param.getDeptNo())) {
	            throw new IllegalArgumentException("이미 존재하는 부서명입니다.");
	        }
	    }
		
		// 이 부분이 없으면 영속성 예외가 발생함
		// member_no가 0일 경우 member에 null을 넣어주기 위함임
		// repository에서 select해서 해당 객체를 넣어주는 느낌으로 이해하고 있으며 해당 객체를 영속 객체라 칭하는 것 같음 - 구글링
		Member chiefMember = null;
	    if(dto.getMember_no() != null) {
	    	chiefMember = memberRepository.findById(dto.getMember_no())
	                                .orElseThrow(() -> new IllegalArgumentException("해당 부서장이 존재하지 않습니다."));
	    }
	    
	    // 위와 마찬가지로 영속성 예외를 회피하는 조건문
	    // 결론적으로, JPA의 Entity와 DB의 TABLE 간의 무결성을 지키기 위해 예외를 띄워주는 것으로 이해함
	    Dept parentDept = null;
	    if(dto.getParent_dept_no() != null) {
	        parentDept = repository.findById(dto.getParent_dept_no())
	                               .orElseThrow(() -> new IllegalArgumentException("해당 상위부서가 존재하지 않습니다."));
	    }
		
		Dept result = repository.save(Dept.builder()
				.deptNo(param.getDeptNo())
				.deptName(dto.getDept_name())
				.member(chiefMember)
				.parentDept(parentDept)
				.deptPhone(dto.getDept_phone())
				.deptLocation(dto.getDept_location())
				.deptStatus(dto.getDept_status())
				.build());
		
		return result;
				
	}
	
}

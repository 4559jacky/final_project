package com.mjc.groupware.member.service;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.dept.repository.DeptRepository;
import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberRepository repository;
	private final DeptRepository deptRepository;
	private final PasswordEncoder passwordEncoder;
	private final DataSource dataSource;
	private final UserDetailsService userDetailsService;
	
	public Member selectMemberOne(MemberDto dto) {
		Member result = repository.findByMemberId(dto.getMember_id());
		
		return result;
	}
	
	public List<Member> selectMemberAll() {
		List<Member> resultList = repository.findAll();
		
		return resultList;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Member createMember(MemberDto dto) {
		Member result = null;
		
		try {
			dto.setMember_pw(passwordEncoder.encode(dto.getMember_pw()));
			
			result = repository.save(dto.toEntity());
		} catch(DataIntegrityViolationException e) {
			throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
		}
		
		return result;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void transferMembersOfDept(Long fromDeptNo, Long toDeptNo) {
		// 삭제인 경우(get_status == 3 인 경우 - 해당 부서의 멤버 구성원을 선택한 부서로 이관시켜주는 작업
		List<Member> members = repository.findAllByDept_DeptNo(fromDeptNo);
			
		Dept transferDept = null;
		if (toDeptNo != null) {
			transferDept = deptRepository.findById(toDeptNo)
					.orElseThrow(() -> new IllegalArgumentException("이관 대상 부서가 존재하지 않습니다."));
		} // 이 또한 영속성 예외를 방지하기 위함 - ID 기준으로 한 번 더 찾아서 그 후에 작업을 시행하는 느낌 - JPA에서 권장하는 방식임
		
		for (Member member : members) {
	        member.changeDept(transferDept);
	    } // 예전에 배웠던 방식인데, Entity에는 Setter를 두지 않으면서 무결성을 유지하고, 도메인 메서드를 하나 만들어서 좀 더 비지니스 로직을 명시적으로 표현할 수 있음
		
	}
	
	// 결재라인 부서의 속한 사원들select
	public List<Member> selectMemberAllByDeptId(Long id) { 
		List<Member> memberList = repository.findAllByDept_DeptNo(id); 
		return memberList;
	}
	
	
}

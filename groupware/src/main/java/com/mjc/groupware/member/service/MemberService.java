package com.mjc.groupware.member.service;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.dept.repository.DeptRepository;
import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.dto.MemberResponseDto;
import com.mjc.groupware.member.dto.MemberSearchDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.entity.Role;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.member.specification.MemberSpecification;
import com.mjc.groupware.pos.entity.Pos;

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
	
	public Member selectMemberOneByMemberNo(MemberDto dto) {
		Member result = repository.findById(dto.getMember_no()).orElse(null);
		
		return result;
	}
	
	public List<Member> selectMemberAll() {
		List<Member> resultList = repository.findAll();
		
		return resultList;
	}
	
	public List<Member> selectMemberAll(MemberSearchDto searchDto) {
		Specification<Member> spec = (root,query,criteriaBuilder) -> null;

		if("".equals(searchDto.getSearch_text()) || searchDto.getSearch_text() == null) {
			// 아무것도 입력하지않으면 findAll() 과 동일함
		} else {
			spec = spec.and(MemberSpecification.memberNameContains(searchDto.getSearch_text()));			
		}

		List<Member> resultList = repository.findAll(spec);
		
		return resultList;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Member createMember(MemberDto dto) {
		Member result = null;
		
		try {
			dto.setMember_pw(passwordEncoder.encode(dto.getMember_pw()));
			
			result = repository.save(Member.builder()
					.memberId(dto.getMember_id())
					.memberPw(dto.getMember_pw())
					.memberName(dto.getMember_name())
					.pos(dto.getPos_no() != 0 ?	Pos.builder().posNo(dto.getPos_no()).build() : null)
					.dept(dto.getDept_no() != 0 ? Dept.builder().deptNo(dto.getDept_no()).build() : null)
					.role(Role.builder().roleNo((long)2).build())
					.status(100)
					.build());
					
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
	
	public void updateMemberPw(MemberDto dto) {
		try {
			Specification<Member> spec = (root, query, criteriaBuilder) -> null;
			spec = spec.and(MemberSpecification.equalMemberNo(dto.getMember_no()));
			
			Member target = repository.findOne(spec).orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다"));
			
			if(!passwordEncoder.matches(dto.getMember_pw(), target.getMemberPw())) {
				throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
			}
			
			target.changePassword(passwordEncoder.encode(dto.getMember_new_pw()));
			
			repository.save(target);
			
			// 비밀번호 수정 후 -> 자동로그인이 있다면 자동로그인을 해제
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "DELETE FROM persistent_logins WHERE username = ?";
			jdbcTemplate.update(sql, dto.getMember_id());
			
			// 비밀번호 수정 후 -> 로그아웃 시키지 않고, 인증정보를 바꾸고 싶을 때 - 비밀번호 외 비교적 간단한 정보 바꿀 때
//			UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(dto.getMember_id());
//			Authentication newAuth = new UsernamePasswordAuthenticationToken(
//					updatedUserDetails,
//					updatedUserDetails.getPassword(),
//					updatedUserDetails.getAuthorities());
//			SecurityContextHolder.getContext().setAuthentication(newAuth);
			
			// 비밀번호 수정 후 -> 로그인 된 사원의 인증 상태를 해제 (즉, 인증이 풀리면서 /login 으로 강제로 끌려들어감)
			SecurityContextHolder.getContext().setAuthentication(null);
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch(Exception e) {
			throw new RuntimeException("비밀번호 수정 중 알 수 없는 문제가 발생했습니다.");
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void updateMemberInfo(MemberDto dto) {
		// 앞서 많이 썼지만 @Transaction + 도메인메소드 응용해서 바뀐 부분만 수정하는 로직 - 이렇게 안 하면 데이터로 넣지 않는 부분에 null이 들어감
		try {
			Member target = repository.findById(dto.getMember_no()).orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
			
			target.updateProfileInfo(
			        dto.getMember_name(),
			        dto.getMember_gender(),
			        dto.getMember_birth(),
			        dto.getMember_phone(),
			        dto.getMember_email(),
			        dto.getMember_addr1(),
			        dto.getMember_addr2(),
			        dto.getMember_addr3()
			    );
			
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch(Exception e) {
			throw new RuntimeException("개인정보 수정 중 알 수 없는 문제가 발생했습니다.");
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void updateMember(MemberResponseDto dto) {
		// 당연하게도 dto에 삽입된 정보만 바꿀 것이므로 @Transaction + 도메인메소드 활용
		try {
			Member target = repository.findById(dto.getMember_no()).orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
			
			target.updateMember(
					Dept.builder().deptNo(dto.getDept_no()).build(),
					Pos.builder().posNo(dto.getPos_no()).build(),
					Role.builder().roleNo(dto.getRole_no()).build(),
					dto.getStatus()
					);
			
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch(Exception e) {
			throw new RuntimeException("사원 정보 수정 중 알 수 없는 문제가 발생했습니다.");
		}
	}
	
	// 특정 부서에 속한 모든 사원들을 조회(직급 순서 기준으로 오름차순, 같다면 PK기준으로 오름차순)
	public List<Member> selectMemberAllByDeptIdByPosOrder(Long id) {
		List<Member> memberList = repository.findAllByDeptNoSortedByPosOrder(id);
		return memberList;
	}
	
	// 결재라인 부서의 속한 사원들select
	public List<Member> selectMemberAllByDeptId(Long id) { 
		List<Member> memberList = repository.findAllByDept_DeptNo(id); 
		return memberList;
	}
	
	
	// 전자서명 저장
	public int createSignatureApi(Long member_no, String signature) {
		
		int result = 0;
		
		try {
			Member entity = repository.findById(member_no).orElse(null);
			MemberDto memberDto = new MemberDto().toDto(entity);
			memberDto.setSignature(signature);
			
			Member member = Member.builder()
								.memberNo(memberDto.getMember_no())
								.memberId(memberDto.getMember_id())
								.memberPw(memberDto.getMember_pw())
								.memberName(memberDto.getMember_name())
								.memberBirth(memberDto.getMember_birth())
								.memberGender(memberDto.getMember_gender())
								.memberAddr1(memberDto.getMember_addr1())
								.memberAddr2(memberDto.getMember_addr2())
								.memberAddr3(memberDto.getMember_addr3())
								.pos(Pos.builder().posNo(memberDto.getPos_no()).build())
								.dept(Dept.builder().deptNo(memberDto.getDept_no()).build())
								.role(Role.builder().roleNo(memberDto.getRole_no()).build())
								.status(memberDto.getStatus())
								.signature(signature)
								.build();
			
			Member saved = repository.save(member);
			
			System.out.println(memberDto);
			
			if(saved != null) {
				result = 1;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}

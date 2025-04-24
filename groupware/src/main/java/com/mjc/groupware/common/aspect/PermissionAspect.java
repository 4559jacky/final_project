package com.mjc.groupware.common.aspect;

import java.util.List;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mjc.groupware.common.annotation.CheckPermission;
import com.mjc.groupware.company.entity.FuncMapping;
import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {
	
	private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);
	
	private final EntityManager entityManager;
	
	@Before("@annotation(checkPermission)")
	public void checkPermission(CheckPermission checkPermission) throws Exception {
		String funcCode = checkPermission.value();
		String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
		
		logger.info("접근권한확인 : 아이디 {} with 기능코드 {}", memberId, funcCode);
		
		if (funcCode == null || funcCode.isEmpty()) {
		    throw new IllegalArgumentException("기능 코드가 필요합니다.");
		}
		
		// 현재 로그인한 사원의 memberId를 조건으로 걸어서 DB에서 객체를 조회
		Member member = entityManager.createQuery("SELECT m FROM Member m WHERE m.memberId = :memberId", Member.class)
				.setParameter("memberId", memberId)
				.getSingleResult();
		
		if (member == null) {
		    throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다.");
		}
		
		if (member.getRole() == null) {
		    throw new IllegalArgumentException("사용자의 역할 정보가 없습니다.");
		}
		
		// member.getRole()을 통해 role을 뽑아내서, 그 역할에 해당하는 func_mappings 를 조회 
		List<FuncMapping> funcMappings = entityManager.createQuery("SELECT fm FROM FuncMapping fm WHERE fm.role = :role", FuncMapping.class)
				.setParameter("role", member.getRole())
				.getResultList();
		
		// 해당 func_code와 일치하는 권한이 있는지 판단
		boolean hasPermission = funcMappings.stream()
				.anyMatch(funcMapping -> funcMapping.getFunc().getFuncCode().equals(funcCode));
		
		if(!hasPermission) {
			throw new AccessDeniedException("해당 기능에 접근할 권한이 없습니다.");
		}
		
	}
	
}

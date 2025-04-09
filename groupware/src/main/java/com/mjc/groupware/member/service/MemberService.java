package com.mjc.groupware.member.service;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberRepository repository;
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
	
}

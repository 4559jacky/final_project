package com.mjc.groupware.member.service;

import javax.sql.DataSource;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
	
	public Member createMember(MemberDto dto) {
		dto.setMember_pw(passwordEncoder.encode(dto.getMember_pw()));
		
		Member result = repository.save(dto.toEntity());
		
		return result;
	}
	
}

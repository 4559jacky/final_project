package com.mjc.groupware.service;

import javax.sql.DataSource;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mjc.groupware.dto.MemberDto;
import com.mjc.groupware.entity.Member;
import com.mjc.groupware.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final DataSource dataSource;
	private final UserDetailsService userDetailsService;
	
	public Member selectMemberOne(MemberDto dto) {
		Member member = repository.findByMemberId(dto.getMember_id());
		
		return member;
	}
	
}

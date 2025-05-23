package com.mjc.groupware.member.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mjc.groupware.company.repository.FuncMappingRepository;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
	
	private final MemberRepository memberRepository;
	
	@Override
	public MemberDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByMemberId(username);
		
		if(member == null) {
			throw new UsernameNotFoundException("존재하지 않는 회원입니다.");
		}
		
		return new MemberDetails(member);
	}
	
}

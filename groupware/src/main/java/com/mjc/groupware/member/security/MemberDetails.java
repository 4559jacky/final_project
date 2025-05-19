package com.mjc.groupware.member.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mjc.groupware.member.entity.Member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class MemberDetails implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	private final Member member;
	
	// 사용자 권한 설정
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(member.getRole().getRoleName()));
		
		return authorities;
	}
	
	// 사용자 PW 반환
	@Override
	public String getPassword() {
		return member.getMemberPw();
	}
	
	// 사용자 ID 반환
	@Override
	public String getUsername() {
		return member.getMemberId();
	}
	
	// 임시 계정, 비활성 계정 체크
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	// 잠금 여부 체크
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	// 만료 여부 체크
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	// 계정 사용 가능 여부 체크
	@Override
	public boolean isEnabled() {
		// 계정이 사용 가능한지 확인하는 로직이 필요하다면 추가할 것
		
		// 로그인 직전 :: 로그인 하려는 멤버 객체의 상태를 뽑아서 300 || 400, 401, 402 || 900 일 경우 차단
		int status = member.getStatus();
	    return !(status == 300 || status >= 400 && status <= 402 || status == 900);
	}
	
}

package com.mjc.groupware.member.security;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {
	
	private final RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        Long memberNo = memberDetails.getMember().getMemberNo();
		
        // Redis에서 해당 memberNo 세션 정보 저장
        String key = "member:session:" + memberNo;	// 예: member:session:123
        Long sessionCount = redisTemplate.opsForValue().decrement(key);	// 세션 수 1 감소
        
        // 세션 수가 0이 되면 Redis에서 키 삭제
        if (sessionCount == null || sessionCount <= 0) {
            redisTemplate.delete(key);
        }
        
		System.out.println("MyLogoutSuccessHandler :: 로그아웃 성공");
		
		response.sendRedirect("/login");
		
	}
	
}

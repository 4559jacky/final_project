package com.mjc.groupware.member.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.mjc.groupware.common.redis.RedisLoginLogService;
import com.mjc.groupware.member.dto.LogRedisDto;
import com.mjc.groupware.member.entity.LoginLog;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.LoginLogRepository;
import com.mjc.groupware.member.repository.MemberRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {
	
	private final MemberRepository memberRepository;
	private final LoginLogRepository loginLogRepository;
	private final RedisLoginLogService redisLoginLogService;
	private final RedisTemplate<String, Object> redisTemplate;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
		Long memberNo = memberDetails.getMember().getMemberNo();
		
		// Null 체크: memberNo가 유효한 값인지 확인
		if (memberNo == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid member.");
			return;
		}
		
		String clientIp = getClientIp(request);
		
		Member member = memberRepository.findById(memberNo)
				.orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다. id: " + memberNo + ", 로그인 시각: " + LocalDateTime.now()));
		
		// MariaDB 저장
		LoginLog log = LoginLog.builder()
                .member(member)
                .loginTime(LocalDateTime.now())
                .loginIp(clientIp)
                .loginAgent(request.getHeader("User-Agent"))
                .build();
        
        loginLogRepository.save(log);
        
        // Redis 저장
        LogRedisDto redisDto = LogRedisDto.builder()
                .loginTime(log.getLoginTime())
                .loginIp(log.getLoginIp())
                .loginAgent(log.getLoginAgent())
                .build();
        
        redisLoginLogService.saveLoginLog(memberNo, redisDto);
        
        // Redis에 "member:session:" + memberNo를 키로 세션 수를 저장 :: 동시로그인 제어
        String key = "member:session:" + memberNo;  // 예: member:session:123
        Long sessionCount = redisTemplate.opsForValue().increment(key, 1);  // 세션 수 1 증가
        
        if (sessionCount > 3) {
        	redisTemplate.opsForValue().decrement(key);
        	String errorMsg = "동시 접속 허용 수(3)를 초과했습니다.";
            response.sendRedirect("/login?error=true&errorMsg=" + URLEncoder.encode(errorMsg, "UTF-8"));
            return;
        }
        
		System.out.println("MyLoginSuccessHandler :: 로그인 성공");
		
		response.sendRedirect("/");
	}
	
	// 클라이언트 IP 가져오는 메서드 (IPv4만 고려)
    private String getClientIp(HttpServletRequest request) {
    	// 기본적으로 원격 주소가 들어옴 (IPv4)
    	String ip = request.getHeader("X-Forwarded-For");
        
    	if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // IP가 여러 개 있을 경우, 첫 번째 유효한 IP를 사용
    	String[] ipArray = ip.split(",");
    	for (String ipPart : ipArray) {
    	    if (!"unknown".equalsIgnoreCase(ipPart.trim())) {
    	        ip = ipPart.trim();
    	        break;
    	    }
    	}
    	
        // IPv6를 IPv4 형식으로 변환하는 코드 (IPv6가 들어오는 경우 == 로컬호스트인 경우)
    	if (ip.equals("::1") || ip.equals("0:0:0:0:0:0:0:1")) {
    	    ip = "127.0.0.1";
    	}
        return ip;
    }
	
}

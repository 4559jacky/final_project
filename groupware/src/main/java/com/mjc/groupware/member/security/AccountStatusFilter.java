package com.mjc.groupware.member.security;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mjc.groupware.member.repository.MemberRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountStatusFilter extends OncePerRequestFilter {

	/*
	 * 상태가 400, 401, 402, 900 인 경우 강제로 로그아웃 시키기 위해서, 매 요청마다 상태를 판단하는 필터 
	 */
	
	private final MemberRepository memberRepository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 무한 리다이렉션을 막기 위해서 인증없이 들어갈 수 있는 페이지는 필터를 거치지 않게 해줘야함 :: 이것 때문에 한참 고생함
		String uri = request.getRequestURI();
		if(uri.startsWith("/login") || uri.startsWith("/logout") || uri.startsWith("/forgetPassword")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		// 인증되지 않은 사용자라면 시큐리티가 절차대로 처리
		if (authentication == null || !authentication.isAuthenticated()) {
			filterChain.doFilter(request, response);
			return;
		}
		
		Object principal = authentication.getPrincipal();
	    if (!(principal instanceof MemberDetails)) {
	        filterChain.doFilter(request, response);
	        return;
	    }
		
		// 이미 인증된 사용자일 경우 - 여기가 중요함 :: memberDetail은 세션으로 관리되기 때문에 DB가 바뀌어도 바로 적용되지 않음 -> DB에서 조회를 해옴 
		MemberDetails memberDetails = (MemberDetails) principal;
		Long memberNo = memberDetails.getMember().getMemberNo();
		int status = memberRepository.findById(memberNo)
		    .map(member -> member.getStatus())
		    .orElse(999); // 999는 예외 상황을 처리해야할 경우를 위해 따로 빼둠
		
		// 상태가 300, 400, 401, 402, 900인 경우 로그아웃 처리
		if ((status == 300 || status >= 400 && status <= 402) || status == 900) {
			// 인증 정보 초기화
			SecurityContextHolder.clearContext();
			// 강제로 세션을 무효화
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}
			// 로그인 페이지로 쫒아냄
			response.sendRedirect("/login?error=true&errorMsg=" + 
				URLEncoder.encode("계정이 비활성화되어 로그아웃되었습니다.", "UTF-8"));
			return;
		}
		
		filterChain.doFilter(request, response);
	}
	
}

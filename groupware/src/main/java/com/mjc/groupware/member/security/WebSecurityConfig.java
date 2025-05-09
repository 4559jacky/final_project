package com.mjc.groupware.member.security;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
	
	private final DataSource dataSource;
	private final AccountStatusFilter accountStatusFilter;
	private final MyLoginSuccessHandler myLoginSuccessHandler;
	private final MyLoginFailureHandler myLoginFailureHandler;
	private final MyLogoutSuccessHandler myLogoutSuccessHandler;
	
	// 정적 리소스 시큐리티 비활성화
	@Bean
	WebSecurityCustomizer configure() {
		return (web -> web.ignoring()
					.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
					.requestMatchers("/assets/**", "/favicon.ico", "/uploads/**")
		);
	}
	// 시큐리티 환경 설정
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService customUserDetailsService) throws Exception {
		http.userDetailsService(customUserDetailsService)
		.addFilterAfter(accountStatusFilter, BasicAuthenticationFilter.class) // 로그인 이후 인증된 사용자 상태 체크 
		.authorizeHttpRequests(requests -> requests
				.requestMatchers("/login","/logout", "/forgetPassword/**").permitAll()
				.requestMatchers("/admin/company/**").hasRole("ADMIN")
				.anyRequest().authenticated())
		.formLogin(login -> login
				.loginPage("/login")
				.successHandler(myLoginSuccessHandler)
				.failureHandler(myLoginFailureHandler))
		.logout(logout -> logout
				.clearAuthentication(true)
				.logoutSuccessHandler(myLogoutSuccessHandler)
				.invalidateHttpSession(true)
				.deleteCookies("remember-me"))
		.rememberMe(rememberMe -> rememberMe.rememberMeParameter("remember-me")
				.tokenValiditySeconds(60*60*24*30)
				.alwaysRemember(false)
				.tokenRepository(tokenRepository()));
		
		return http.build();
	}
	
	// 데이터베이스 접근 Bean 등록 - Spring Security는 JDBC 방식을 필요로함
	@Bean
	PersistentTokenRepository tokenRepository() {
		JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource);
		return jdbcTokenRepository;
	}
	
	// 비밀번호 암호화
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// 인증 관리자 설정
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	 // 세션 소멸 감지를 위한 필수 등록
    @Bean
    static HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
    
}

package com.mjc.groupware.member.security;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
	
	private final DataSource dataSource;
	
	// 정적 리소스 시큐리티 비활성화
	@Bean
	WebSecurityCustomizer configure() {
		return (web -> web.ignoring()
					.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
					.requestMatchers("/assets/**", "/favicon.ico")
		);
	}
	
	// 시큐리티 환경 설정
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService customUserDetailsService) throws Exception {
		http.userDetailsService(customUserDetailsService)
		.authorizeHttpRequests(requests -> requests
				.requestMatchers("/login","/logout").permitAll()
				.requestMatchers("/member/create", "/pos/create", "/dept/create").hasRole("ADMIN")
				.anyRequest().authenticated())
		.formLogin(login -> login
				.loginPage("/login")
				.successHandler(new MyLoginSuccessHandler())
				.failureHandler(new MyLoginFailureHandler()))
		.logout(logout -> logout
				.clearAuthentication(true)
				.logoutSuccessUrl("/login")
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
	
}

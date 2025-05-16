package com.mjc.groupware.member.dto;

import java.time.LocalDateTime;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class LogRedisDto {
	
	private LocalDateTime loginTime;
    private String loginIp;
    private String loginAgent;
	
}

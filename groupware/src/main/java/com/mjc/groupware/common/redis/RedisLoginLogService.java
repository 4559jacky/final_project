package com.mjc.groupware.common.redis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.groupware.member.dto.LogRedisDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisLoginLogService {
	
	private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
	
    public void saveLoginLog(Long memberNo, LogRedisDto dto) {
    	// Redis 저장 로직
    	try {
            String jsonValue = objectMapper.writeValueAsString(dto);
            
            // memberNo와 loginTime을 결합하여 key로 사용하고자 함 :: 동시에 여러명이 로그인하는 상황에 대한 대처
            String key = "login:" + memberNo + ":" + dto.getLoginTime().toString(); // 예: "login:1234:2025-05-08T12:34:56"
            
            redisTemplate.opsForValue().set(key, jsonValue);
            redisTemplate.expire(key, 90, TimeUnit.DAYS); // TTL 90일
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 중 JSON 변환 에러", e);
        }
    }
    
    public LogRedisDto getLoginLog(Long memberNo) {
    	// Redis 조회 로직 - 기본
        Object rawValue = redisTemplate.opsForValue().get("login:" + memberNo);
        if (rawValue == null) return null;
        
        try {
            return objectMapper.readValue(rawValue.toString(), LogRedisDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 조회 중 JSON 파싱 에러", e);
        }
    }
    
    public List<LogRedisDto> getLoginLogs(Long memberNo, LocalDate loginStartDate, LocalDate loginEndDate) {
        // Redis 조회 로직 - 로그인 이력 리스트
        List<LogRedisDto> loginLogs = new ArrayList<>();
        
        // 특정 멤버넘버의 모든 key를 조회해서 Set에 담아줌
        Set<String> allKeys = redisTemplate.keys("login:" + memberNo + ":*");
        
        if (allKeys != null && !allKeys.isEmpty()) {
            for (String key : allKeys) {
                try {
                    String value = (String) redisTemplate.opsForValue().get(key);
                    LogRedisDto logDto = objectMapper.readValue(value, LogRedisDto.class);
                    LocalDateTime loginTime = logDto.getLoginTime();
                    
                    // 날짜 범위 필터링
                    if ((loginStartDate == null || !loginTime.toLocalDate().isBefore(loginStartDate)) &&
                        (loginEndDate == null || !loginTime.toLocalDate().isAfter(loginEndDate))) {
                        loginLogs.add(logDto);
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Redis 조회 중 JSON 파싱 에러", e);
                }
            }
            
            loginLogs.sort((a, b) -> b.getLoginTime().compareTo(a.getLoginTime())); // 시간 내림차순으로 정렬
        }
        
        return loginLogs;
    }
    
}

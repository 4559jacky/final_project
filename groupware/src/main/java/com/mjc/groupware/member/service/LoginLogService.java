package com.mjc.groupware.member.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mjc.groupware.member.dto.LogPageDto;
import com.mjc.groupware.member.entity.LoginLog;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.LoginLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginLogService {
	
	private final LoginLogRepository repository;
	
	public Page<LoginLog> findByMemberOrderByLoginTimeDesc(Member member, LogPageDto pageDto) {
		Pageable pageable = PageRequest.of(pageDto.getNowPage()-1, pageDto.getNumPerPage(), Sort.by("loginTime").descending());
		
		Page<LoginLog> resultList = repository.findByMemberOrderByLoginTimeDesc(member, pageable);
		
		return resultList;
	}
	
}

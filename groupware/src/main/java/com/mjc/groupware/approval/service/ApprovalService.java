package com.mjc.groupware.approval.service;

import org.springframework.stereotype.Service;

import com.mjc.groupware.approval.dto.ApprovalFormDto;
import com.mjc.groupware.approval.entity.ApprovalForm;
import com.mjc.groupware.approval.repository.ApprovalFormRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ApprovalService {
	
	private final ApprovalFormRepository repository;

	public int createApprovalApi(ApprovalFormDto dto) {
		int result = 0;
		try {
			ApprovalForm entity = dto.toEntity();
			ApprovalForm saved = repository.save(entity);
			if(saved != null) {
				result = 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}

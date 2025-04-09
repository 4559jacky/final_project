package com.mjc.groupware.shared.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mjc.groupware.shared.dto.SharedDto;
import com.mjc.groupware.shared.entity.Shared;
import com.mjc.groupware.shared.repository.SharedRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SharedService {

	private final SharedRepository repository;
	
	public int createSharedApi(SharedDto dto) {
		int result = 0;
		try {
			if (dto.getMember_no() == null) {
	            dto.setMember_no(1L); // Spring Security 없으면 임시로 1
	        }
			Shared entity = dto.toEntity();
			Shared saved = repository.save(entity);
			if(saved != null) {
				result = 1;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 게시판 조회
	public List<Shared> getSharedList() {
		return repository.findAll(Sort.by(Sort.Direction.DESC, "sharedNo"));
	}
}

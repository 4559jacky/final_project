package com.mjc.groupware.shared.service;

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
}

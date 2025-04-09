package com.mjc.groupware.shared.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.shared.dto.AttachDto;
import com.mjc.groupware.shared.dto.SharedDto;
import com.mjc.groupware.shared.entity.Attach;
import com.mjc.groupware.shared.entity.Shared;
import com.mjc.groupware.shared.repository.AttachRepository;
import com.mjc.groupware.shared.repository.SharedRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SharedService {

	private final SharedRepository repository;
	private final AttachRepository attachRepository;
	private final AttachService attachService;
	
	
	
	@Transactional(rollbackFor = Exception.class)
	public int createSharedApi(SharedDto dto, List<AttachDto> attachList) {
		int result = 0;
		try {
			Shared entity = dto.toEntity();
			Shared saved = repository.save(entity);
			
			Long sharedNo = saved.getSharedNo();
			if(attachList.size() != 0) {
				for(AttachDto attachDto : attachList) {
					attachDto.setShared_no(sharedNo);
					Attach attach = attachDto.toEntity();
					attachRepository.save(attach);
				}
			}
			result = 1;
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

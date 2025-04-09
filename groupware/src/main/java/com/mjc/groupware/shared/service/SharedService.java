package com.mjc.groupware.shared.service;

import com.mjc.groupware.shared.dto.SharedDto;
import com.mjc.groupware.shared.entity.Shared;
import com.mjc.groupware.shared.repository.SharedRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SharedService {

	private final SharedRepository repository;

	// 게시글 저장
	public void saveShared(SharedDto sharedDto) {
		Shared shared = sharedDto.toEntity();
		//shared.setViews(0); // 기본 조회수
		repository.save(shared);
	}

	// 게시글 목록 조회
	public List<Shared> getSharedList() {
		return repository.findAll(Sort.by(Sort.Direction.DESC, "sharedNo"));
	}
}

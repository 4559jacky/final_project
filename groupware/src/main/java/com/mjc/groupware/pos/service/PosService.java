package com.mjc.groupware.pos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.pos.dto.PosDto;
import com.mjc.groupware.pos.dto.PosOrderDto;
import com.mjc.groupware.pos.entity.Pos;
import com.mjc.groupware.pos.repository.PosRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PosService {
	
	private final PosRepository repository;
	
	@Transactional(rollbackFor = Exception.class)
	public Pos createPos(PosDto dto) {
		
		if (repository.existsByPosName(dto.getPos_name())) {
		    throw new IllegalArgumentException("이미 존재하는 직급명입니다.");
		}
		
		int maxOrder = Optional.ofNullable(repository.findMaxOrder()).orElse(0);
		
		Pos entity = Pos.builder()
				.posName(dto.getPos_name())
				.posOrder((long)(maxOrder + 1))
				.build();
		
		return repository.save(entity);
	}
	
	public List<Pos> selectPosAll() {
		List<Pos> resultList = repository.findAllOrderByPosOrderAsc();
		
		return resultList;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void updateOrder(List<PosOrderDto> posOrderList) {
		for (PosOrderDto dto : posOrderList) {
	        Pos pos = repository.findById(dto.getPosNo())
	                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직급입니다."));

	        pos.changeOrder(dto.getOrder().longValue()); // 도메인 메서드 호출
	    }
	}
	
}

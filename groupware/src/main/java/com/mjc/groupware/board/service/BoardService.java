package com.mjc.groupware.board.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.dto.PageDto;
import com.mjc.groupware.board.dto.SearchDto;
import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.repository.BoardRepository;
import com.mjc.groupware.board.specification.BoardSpecification;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BoardService {
	
	private final BoardRepository repository;
//	private final AttachRepository attachRepository;
//	private final AttachService attachService;
	
	// 게시글 삭제
	public int deleteBoard(Long id) {
		int result = 0;
		try {
			Board target = repository.findById(id).orElse(null);
			if(target != null) {
				repository.deleteById(id);
			}
			result = 1; 
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 게시글 단일 조회
	public Board selectBoardOne(Long id) {
		return repository.findById(id).orElse(null);
	}
	
	public Page<Board> selectBoardAll(SearchDto searchDto, PageDto pageDto){
		
		Pageable pageable = PageRequest.of(pageDto.getNowPage()-1, pageDto.getNumPerPage(), Sort.by("regDate").descending());
		if(searchDto.getOrder_type() == 2) {
			pageable = PageRequest.of(pageDto.getNowPage()-1, pageDto.getNumPerPage(), Sort.by("regDate").ascending());
		}
		
		Specification<Board> spec = (root,query,criteriaBuilder) -> null; 
		if(searchDto.getSearch_type() == 1) {
			spec = spec.and(BoardSpecification.boardTitleContains(searchDto.getSearch_text()));
		} else if(searchDto.getSearch_type() == 2) {
			spec = spec.and(BoardSpecification.boardContentContains(searchDto.getSearch_text()));
		} else if(searchDto.getSearch_type() == 3) {
			spec = spec.and(BoardSpecification.boardTitleContains(searchDto.getSearch_text()))
					.or(BoardSpecification.boardContentContains(searchDto.getSearch_text()));
		}
		Page<Board> list = repository.findAll(spec,pageable);
		return list;
		
	}
	public Board updateBoard(BoardDto param) {
		Board result = null;
		Board target = repository.findById(param.getBoard_no()).orElse(null);
		if(target != null) {
			//
		}
		return result;
	}

}
	



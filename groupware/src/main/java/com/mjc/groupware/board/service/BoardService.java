package com.mjc.groupware.board.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


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

	// 게시글 등록
	public void createBoard(BoardDto board_dto) {
	    Board board = Board.builder()
	            .board_title(board_dto.getBoard_title())
	            .board_content(board_dto.getBoard_content())
	            .member_no(board_dto.getMember_no())
	            .views(0)
	            .build();
	    repository.save(board);
	}

	// 게시글 삭제
	public int deleteBoard(Long board_no) {
		int result = 0;
		try {
			Board target = repository.findById(board_no).orElse(null);
			if (target != null) {
				repository.deleteById(board_no);
				result = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 게시글 단일 조회
	public Board selectBoardOne(Long board_no) {
	    Board board = repository.findById(board_no).orElse(null);
	    if (board != null) {
	        board.incrementViews();
	        repository.save(board);
	    }
	    return board;
	}

	// 게시글 목록 조회
	public Page<Board> selectBoardAll(SearchDto search_dto, PageDto page_dto) {
	    Sort sort = Sort.by(Sort.Direction.DESC, "reg_date");
	    if (search_dto.getOrder_type() == 2) {
	        sort = Sort.by(Sort.Direction.ASC, "reg_date");
	    } else if (search_dto.getOrder_type() == 3) {
	        sort = Sort.by(Sort.Direction.DESC, "views");
	    }

	    Pageable pageable = PageRequest.of(page_dto.getNowPage() - 1, page_dto.getNumPerPage(), sort);
	    Specification<Board> spec = Specification.where(null);

	    if (search_dto.getSearch_type() == 1) {
	        spec = spec.and(BoardSpecification.boardTitleContains(search_dto.getSearch_text()));
	    } else if (search_dto.getSearch_type() == 2) {
	        spec = spec.and(BoardSpecification.boardContentContains(search_dto.getSearch_text()));
	    } else if (search_dto.getSearch_type() == 3) {
	        spec = spec.and(BoardSpecification.boardTitleContains(search_dto.getSearch_text()))
	                   .or(BoardSpecification.boardContentContains(search_dto.getSearch_text()));
	    }

	    return repository.findAll(spec, pageable);
	}

	// 게시글 수정
	public Board updateBoard(BoardDto board_dto) {
	    Board entity = repository.findById(board_dto.getBoard_no()).orElse(null);
	    if (entity != null) {
	        entity.setBoard_title(board_dto.getBoard_title());
	        entity.setBoard_content(board_dto.getBoard_content());
	        return repository.save(entity);
	    }
	    return null;
	}
}
	



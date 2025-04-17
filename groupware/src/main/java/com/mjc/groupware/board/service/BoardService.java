package com.mjc.groupware.board.service;

import java.time.LocalDateTime;
import java.util.Optional;

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
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository repository;
    private final MemberRepository memberRepository;
    private final BoardAttachService boardAttachService;

    // 게시글 등록
    @Transactional(rollbackFor = Exception.class)
    public Board createBoard(BoardDto dto) {
        Member member = memberRepository.findById(dto.getMember_no()).orElse(null);

        Board board = Board.builder()
                .boardTitle(dto.getBoard_title())
                .boardContent(dto.getBoard_content())
                .boardStatus("N")
                .member(Member.builder().memberNo(member.getMemberNo()).build())
                .build();

        return repository.save(board); // 저장된 Board 반환!
    }
    
    
 // 게시글 상세 조회
    public Optional<Board> selectBoardOne(Long boardNo) {
        return repository.findById(boardNo);  // Optional로 반환
    }
    
 // 게시글 조회수 증가
    public void viewCount(Long boardNo) {
        Optional<Board> optionalBoard = repository.findById(boardNo);

        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();
            board.setViews(board.getViews() + 1);  // 조회수 1 증가
            repository.save(board);  // 변경된 조회수 저장
        }
    }
    
    
    
    // 게시글 목록 검색,정렬,페이징
    public Page<Board> selectBoardAll(SearchDto searchDto, PageDto pageDto) {

        // 정렬 방식 설정(최신순,오래된순,조회순 정렬 가능해짐) - 2025-04-16(수요일) 정렬 완료
    	Sort sort;
    	if (searchDto.getOrder_type() == 1) { // 최신순
    	    sort = Sort.by(Sort.Direction.DESC, "regDate");
    	} else if (searchDto.getOrder_type() == 2) { // 오래된순
    	    sort = Sort.by(Sort.Direction.ASC, "regDate");
    	} else if (searchDto.getOrder_type() == 3) { // 조회순
    	    sort = Sort.by(Sort.Direction.DESC, "views");
    	} else {
    	    sort = Sort.by(Sort.Direction.DESC, "regDate"); // 기본값: 최신순
    	}
    	
    	Pageable pageable = PageRequest.of(pageDto.getNowPage() -1, pageDto.getNumPerPage(), sort);

        // 삭제되지 않은 게시글만 조회
        Specification<Board> spec = Specification.where(
            (root, query, cb) -> cb.equal(root.get("boardStatus"), "N")
        );

        // 검색 조건 추가
        String keyword = searchDto.getSearch_text();
        int searchType = searchDto.getSearch_type();

        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType) {
                case 1: spec = spec.and(BoardSpecification.boardTitleContains(keyword)); break;
                case 2: spec = spec.and(BoardSpecification.boardContentContains(keyword)); break;
                case 3: spec = spec.and(BoardSpecification.boardTitleContains(keyword)
                            .or(BoardSpecification.boardContentContains(keyword))); break;
            }
        }
        return repository.findAll(spec, pageable);
    }
    

    // 게시글 수정 서비스
    @Transactional
    public Board updateBoard(BoardDto boardDto) {
        // 게시글을 DB에서 조회 (게시글이 존재하지 않으면 예외 발생)
        Board board = repository.findById(boardDto.getBoard_no())
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        // 1. 게시글 수정
        board.setBoardTitle(boardDto.getBoard_title());
        board.setBoardContent(boardDto.getBoard_content());
        // 수정일자 업데이트(처음 게시글 작성하면 수정일이 나오지 않음 / 수정을 하면 그때 수정일자가 게시글 목록에 수정일자가 찍힘)
        board.setModDate(LocalDateTime.now());  

        // 2. 파일 삭제 처리
        if (boardDto.getDeleteFiles() != null && !boardDto.getDeleteFiles().isEmpty()) {
            boardAttachService.deleteFiles(boardDto.getDeleteFiles());  // 삭제할 파일 처리
        }

        // 3. 수정된 게시글 저장
        return repository.save(board);  // 수정된 게시글을 DB에 저장
    }
    
    // 게시글 삭제("N" -> "Y" 게시글 목록에서 삭제되면 데이터베이스 안에 삭제 여부가 "N" -> "Y"로 바뀜)
    @Transactional
    public void deleteBoard(Long boardNo) {
        Board board = repository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        board.setBoardStatus("Y"); // 삭제 상태로 변경
        board.setModDate(LocalDateTime.now());
        
        repository.save(board);
    }

}
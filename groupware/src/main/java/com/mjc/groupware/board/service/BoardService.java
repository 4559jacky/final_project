package com.mjc.groupware.board.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.dto.PageDto;
import com.mjc.groupware.board.dto.SearchDto;
import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.entity.BoardAttach;
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
    public Board createBoard(BoardDto dto, List<MultipartFile> files) {
        Member member = memberRepository.findById(dto.getMember_no()).orElse(null);
        if (member == null) throw new RuntimeException("회원 정보를 찾을 수 없습니다.");

        Board board = Board.builder()
                .boardTitle(dto.getBoard_title())
                .boardContent(dto.getBoard_content())
                .boardStatus("N")
                .member(member)
                .build();

        // 이 부분에 추가
        if (board.getAttachList() == null) {
            board.setAttachList(new ArrayList<>());
        }

        Board savedBoard = repository.save(board);

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    BoardAttach attach = boardAttachService.uploadFile(file, savedBoard.getBoardNo());
                    if (attach != null) {
                        savedBoard.getAttachList().add(attach);
                    }
                }
            }
        }
        return savedBoard;
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

    // 게시글 목록 검색, 정렬, 페이징
    public Page<Board> selectBoardAll(SearchDto searchDto, PageDto pageDto) {
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

        Pageable pageable = PageRequest.of(pageDto.getNowPage() - 1, pageDto.getNumPerPage(), sort);

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
    public Board updateBoard(BoardDto boardDto, List<MultipartFile> files) {
        Board board = repository.findById(boardDto.getBoard_no())
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        board.setBoardTitle(boardDto.getBoard_title());
        board.setBoardContent(boardDto.getBoard_content());
        board.setModDate(LocalDateTime.now());

        // 삭제할 파일 처리
        if (boardDto.getDelete_files() != null && !boardDto.getDelete_files().isEmpty()) {
            boardAttachService.deleteFiles(boardDto.getDelete_files());
        }

        // 새 파일 업로드 처리
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    BoardAttach attach = boardAttachService.uploadFile(file, board.getBoardNo());
                    if (attach != null) {
                        board.getAttachList().add(attach);
                    }
                }
            }
        }

        return repository.save(board);
    }
    

    // 게시글 삭제 ("N" -> "Y" 게시글 목록에서 삭제되면 데이터베이스 안에 삭제 여부가 "N" -> "Y"로 바뀜)
    @Transactional
    public void deleteBoard(Long boardNo) {
        Board board = repository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        board.setBoardStatus("Y"); // 삭제 상태로 변경
        board.setModDate(LocalDateTime.now());

        repository.save(board);
    }

}
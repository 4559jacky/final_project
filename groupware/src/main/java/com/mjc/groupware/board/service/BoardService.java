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

    /**
     * 게시글 생성 - 고정글 여부 포함
     */
    @Transactional
    public Board createBoard(BoardDto dto, List<MultipartFile> files) {
        Member member = memberRepository.findById(dto.getMember_no())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Board board = Board.builder()
                .boardTitle(dto.getBoard_title())
                .boardContent(dto.getBoard_content())
                .boardStatus("N") // 기본 상태 'N'
                .isFixed(dto.getIs_fixed() != null ? dto.getIs_fixed() : false)
                .member(member)
                .build();

        if (board.getAttachList() == null) {
            board.setAttachList(new ArrayList<>());
        }

        Board savedBoard = repository.save(board);

        // 첨부파일이 있으면 저장
        if (files != null && !files.isEmpty()) {
            List<BoardAttach> attaches = boardAttachService.uploadFiles(files, savedBoard.getBoardNo());
            savedBoard.getAttachList().addAll(attaches);
        }

        return savedBoard;
    }

    /**
     * 단일 게시글 조회
     */
    @Transactional(readOnly = true)
    public Optional<Board> selectBoardOne(Long boardNo) {
        return repository.findById(boardNo);
    }

    /**
     * 조회수 증가
     */
    @Transactional
    public void updateViews(Long boardNo) {
        repository.updateViews(boardNo);
    }

    /**
     * 게시글 목록 조회 (검색 및 페이징 포함)
     */
    public Page<Board> selectBoardAll(SearchDto searchDto, PageDto pageDto) {
        Sort sort;

        if (searchDto.getOrder_type() == 1) { // 최신순
            sort = Sort.by(Sort.Direction.DESC, "regDate");
        } else if (searchDto.getOrder_type() == 2) { // 오래된순
            sort = Sort.by(Sort.Direction.ASC, "regDate");
        } else if (searchDto.getOrder_type() == 3) { // 조회순
            sort = Sort.by(Sort.Direction.DESC, "views");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "regDate"); // 기본 최신순
        }

        Pageable pageable = PageRequest.of(pageDto.getNowPage() - 1, pageDto.getNumPerPage(), sort);

        Specification<Board> spec = Specification.where(
                (root, query, cb) -> cb.equal(root.get("boardStatus"), "N")
        );

        String keyword = searchDto.getSearch_text();
        int searchType = searchDto.getSearch_type();

        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType) {
                case 1:
                    spec = spec.and(BoardSpecification.boardTitleContains(keyword));
                    break;
                case 2:
                    spec = spec.and(BoardSpecification.boardContentContains(keyword));
                    break;
                case 3:
                    spec = spec.and(BoardSpecification.boardTitleContains(keyword)
                            .or(BoardSpecification.boardContentContains(keyword)));
                    break;
            }
        }
        return repository.findAll(spec, pageable);
    }

    /**
     * 고정글 목록 조회
     */
    public List<Board> selectFixedBoardList() {
        return repository.findByBoardStatusAndIsFixed("N", true);
    }

    /**
     * 게시글 수정
     */
    @Transactional(rollbackFor = Exception.class)
    public Board updateBoard(BoardDto boardDto, List<MultipartFile> files) {
        Board board = repository.findById(boardDto.getBoard_no())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        board.setBoardTitle(boardDto.getBoard_title());
        board.setBoardContent(boardDto.getBoard_content());
        board.setIsFixed(boardDto.getIs_fixed() != null ? boardDto.getIs_fixed() : false);
        board.setModDate(LocalDateTime.now());

        // 삭제할 파일 처리
        List<Long> deleteFiles = boardDto.getDelete_files();
        if (deleteFiles != null && !deleteFiles.isEmpty()) {
            boardAttachService.deleteFiles(deleteFiles);
        }

        // 새로 업로드된 파일 처리
        if (files != null && !files.isEmpty()) {
            List<BoardAttach> newAttaches = boardAttachService.uploadFiles(files, board.getBoardNo());
            board.getAttachList().addAll(newAttaches);
        }

        return repository.save(board);
    }

    /**
     * 게시글 삭제 (논리 삭제)
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBoard(Long boardNo) {
        Board board = repository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        if ("Y".equals(board.getBoardStatus())) {
            throw new RuntimeException("이미 삭제된 게시글입니다.");
        }

        board.setBoardStatus("Y"); // 삭제 상태로 변경
        board.setModDate(LocalDateTime.now());

        repository.save(board);
    }
}
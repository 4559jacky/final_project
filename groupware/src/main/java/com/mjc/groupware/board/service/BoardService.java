package com.mjc.groupware.board.service;

import com.mjc.groupware.board.dto.BoardAttachDto;
import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.dto.PageDto;
import com.mjc.groupware.board.dto.SearchDto;
import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardAttachService boardAttachService;

    /**
     * 게시글 등록 + 첨부파일 저장
     */
    @Transactional
    public void createBoard(BoardDto dto, MultipartFile[] files) {
        Board board = dto.toEntity(); // DTO → Entity 변환
        boardRepository.save(board); // 게시글 저장
        boardAttachService.saveAttachFiles(board.getBoardNo(), files); // 첨부파일 저장
    }

    /**
     * 게시글 수정 + 첨부파일 추가 저장
     */
    @Transactional
    public void updateBoard(BoardDto dto, MultipartFile[] files) {
        Board board = boardRepository.findById(dto.getBoard_no())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        board.updateFromDto(dto); // 기존 게시글 엔티티에 DTO 정보 반영
        boardRepository.save(board); // 수정사항 저장
        boardAttachService.saveAttachFiles(board.getBoardNo(), files); // 첨부파일 저장
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    @Transactional
    public void deleteBoard(Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 게시글이 존재하지 않습니다."));
        board.setBoardStatus("D"); // 상태값 '삭제됨(D)'으로 설정
        board.setModDate(LocalDateTime.now()); // 수정일 갱신
        boardRepository.save(board);
    }

    /**
     * 게시글 상세 조회 (조회수 증가 포함)
     */
    @Transactional
    public BoardDto selectBoardDetail(Long boardNo) {
        Board board = boardRepository.findById(boardNo)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        board.setViews(board.getViews() + 1); // 조회수 +1
        return BoardDto.fromEntity(board); // Entity → DTO 변환
    }

    /**
     * 게시글 목록 조회 (검색 + 정렬 + soft delete 제외)
     */
    public List<BoardDto> selectBoardList(SearchDto searchDto) {
        List<Board> boards;

        String keyword = searchDto.getSearchKeyword();
        String type = searchDto.getSearchType();

        // 검색 조건에 따라 게시글 검색
        if (type != null && keyword != null && !keyword.isEmpty()) {
            switch (type) {
                case "title":
                    boards = boardRepository.findByBoardTitleContaining(keyword);
                    break;
                case "content":
                    boards = boardRepository.findByBoardContentContaining(keyword);
                    break;
                default:
                    boards = boardRepository.findAll();
            }
        } else {
            boards = boardRepository.findAll();
        }

        return boards.stream()
                .filter(b -> !"D".equals(b.getBoardStatus())) // 삭제된 게시물 제외
                .sorted((b1, b2) -> {
                    // 정렬 기준에 따라 분기
                    switch (searchDto.getOrder_type()) {
                        case 1: return b2.getRegDate().compareTo(b1.getRegDate()); // 최신순
                        case 2: return b1.getRegDate().compareTo(b2.getRegDate()); // 오래된순
                        default: return Integer.compare(b2.getViews(), b1.getViews()); // 조회수순
                    }
                })
                .map(BoardDto::fromEntity) // Entity → DTO 변환
                .collect(Collectors.toList());
    }

    /**
     * 페이징 정보 계산
     */
    public PageDto selectBoardPage(SearchDto searchDto) {
        int totalListSize = boardRepository.countActiveBoards(); // 삭제되지 않은 전체 게시글 수
        int totalPage = (int) Math.ceil((double) totalListSize / searchDto.getNumPerPage());

        PageDto pageDto = new PageDto();
        pageDto.setNowPage(searchDto.getNowPage());
        pageDto.setTotalPage(totalPage);
        pageDto.calcPaging(); // 시작 페이지, 끝 페이지 등 계산

        return pageDto;
    }

    /**
     * 게시글에 대한 첨부파일 목록 조회
     */
    public List<BoardAttachDto> selectAttachList(Long boardNo) {
        return boardAttachService.selectAttachList(boardNo);
    }
}

// 소프트 삭제
//@Transactional
//public void deleteBoardSoft(Long boardNo) {
//    Board board = boardRepository.findById(boardNo)
//                      .orElseThrow(() -> new RuntimeException("게시글 없음"));
//    board.setDeletedYn("Y");
//    boardRepository.save(board);
//}




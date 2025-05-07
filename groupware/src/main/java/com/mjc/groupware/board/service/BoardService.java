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
import com.mjc.groupware.vote.dto.VoteCreateRequest;
import com.mjc.groupware.vote.dto.VoteDto;
import com.mjc.groupware.vote.dto.VoteOptionDto;
import com.mjc.groupware.vote.entity.Vote;
import com.mjc.groupware.vote.entity.VoteOption;
import com.mjc.groupware.vote.repository.VoteOptionRepository;
import com.mjc.groupware.vote.repository.VoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository repository;
    private final MemberRepository memberRepository;
    private final BoardAttachService boardAttachService;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;

    @Transactional
    public Board createBoard(BoardDto dto, List<MultipartFile> files, VoteCreateRequest voteRequest) {
        Member member = memberRepository.findById(dto.getMember_no())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Board board = Board.builder()
                .boardTitle(dto.getBoard_title())
                .boardContent(dto.getBoard_content())
                .boardStatus("N")
                .isFixed(Boolean.TRUE.equals(dto.getIs_fixed()))
                .member(member)
                .build();

        board.setAttachList(new ArrayList<>());
        Board savedBoard = repository.save(board);

        if (files != null && !files.isEmpty()) {
            List<BoardAttach> attaches = boardAttachService.uploadFiles(files, savedBoard.getBoardNo());
            savedBoard.getAttachList().addAll(attaches);
        }

        if (voteRequest != null && voteRequest.getVoteDto() != null) {
            VoteDto voteDto = voteRequest.getVoteDto();

            Vote vote = Vote.builder()
                    .board(savedBoard)
                    .voteTitle(voteDto.getVote_title())
                    .isMultiple(voteDto.getIs_multiple())
                    .isAnonymous(voteDto.getIs_anonymous())
                    .endDate(voteDto.getEnd_date())
                    .build();

            Vote savedVote = voteRepository.save(vote);

            if (voteRequest.getOptions() != null) {
                for (VoteOptionDto optDto : voteRequest.getOptions()) {
                    VoteOption option = new VoteOption();
                    option.setVote(savedVote);
                    option.setOptionText(optDto.getOption_text());
                    option.setOrderNo(optDto.getOrder_no());

                    voteOptionRepository.save(option);
                }
            }
        }

        return savedBoard;
    }

    @Transactional(readOnly = true)
    public Optional<Board> selectBoardOne(Long boardNo) {
        updateViews(boardNo);
        return repository.findById(boardNo);
    }

    @Transactional
    public void updateViews(Long boardNo) {
        repository.updateViews(boardNo);
    }

    public Page<Board> selectBoardAll(SearchDto searchDto, PageDto pageDto) {
        Sort sort;
        if (searchDto.getOrder_type() == 1) {
            sort = Sort.by(Sort.Direction.DESC, "regDate");
        } else if (searchDto.getOrder_type() == 2) {
            sort = Sort.by(Sort.Direction.ASC, "regDate");
        } else if (searchDto.getOrder_type() == 3) {
            sort = Sort.by(Sort.Direction.DESC, "views");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "regDate");
        }

        Pageable pageable = PageRequest.of(pageDto.getNowPage() - 1, pageDto.getNumPerPage(), sort);

        Specification<Board> spec = Specification.where(
            (root, query, cb) -> cb.equal(root.get("boardStatus"), "N")
        );
        spec = spec.and((root, query, cb) -> cb.isFalse(root.get("isFixed")));

        String keyword = searchDto.getSearch_text();
        int searchType = searchDto.getSearch_type();

        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType) {
                case 1: spec = spec.and(BoardSpecification.boardTitleContains(keyword)); break;
                case 2: spec = spec.and(BoardSpecification.boardContentContains(keyword)); break;
                case 3: spec = spec.and(BoardSpecification.boardTitleContains(keyword)
                        .or(BoardSpecification.boardContentContains(keyword)));
                        break;
            }
        }

        return repository.findAll(spec, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public Board updateBoard(BoardDto boardDto, List<MultipartFile> files) {
        Board board = repository.findById(boardDto.getBoard_no())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        board.setBoardTitle(boardDto.getBoard_title());
        board.setBoardContent(boardDto.getBoard_content());
        board.setIsFixed(boardDto.getIs_fixed() != null ? boardDto.getIs_fixed() : false);
        board.setModDate(LocalDateTime.now());

        List<Long> deleteFiles = boardDto.getDelete_files();
        if (deleteFiles != null && !deleteFiles.isEmpty()) {
            boardAttachService.deleteFiles(deleteFiles);
        }

        if (files != null && !files.isEmpty()) {
            List<BoardAttach> newAttaches = boardAttachService.uploadFiles(files, board.getBoardNo());
            board.getAttachList().addAll(newAttaches);
        }

        return repository.save(board);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBoard(Long boardNo) {
        Board board = repository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        if ("Y".equals(board.getBoardStatus())) {
            throw new RuntimeException("이미 삭제된 게시글입니다.");
        }

        board.setBoardStatus("Y");
        board.setModDate(LocalDateTime.now());

        repository.save(board);

        List<Board> fixedList = repository.findByIsFixedTrueOrderByRegDateDesc();
        fixedList.removeIf(fixedBoard -> fixedBoard.getBoardNo().equals(boardNo));
    }

    public List<Board> selectFixedBoardList() {
        return repository.findByIsFixedTrueAndBoardStatusNot("Y", Sort.by(Sort.Order.desc("regDate")));
    }
}

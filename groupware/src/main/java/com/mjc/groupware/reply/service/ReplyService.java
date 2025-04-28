package com.mjc.groupware.reply.service;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.repository.BoardRepository;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.reply.dto.ReplyDto;
import com.mjc.groupware.reply.entity.Reply;
import com.mjc.groupware.reply.repository.ReplyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    
    // 댓글 생성(일반 댓글)
    @Transactional
    public void replyCreate(ReplyDto replyDto) {
    	// 게시글 조회
        Board board = boardRepository.findById(replyDto.getBoard_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        // 회원 조회
        Member member = memberRepository.findById(replyDto.getMember_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        // 댓글 엔티티 생성
        Reply reply = Reply.builder()
                .replyContent(replyDto.getReply_content())
                .replyStatus("N") // 정상 댓글
                .board(board)
                .member(member)
                .regDate(LocalDateTime.now()) // 작성 시간
                .build();

        replyRepository.save(reply); // 댓글 저장
    }
    
    
    // 대댓글 생성(부모 댓글 존재)
    @Transactional
    public void replyCreateSub(ReplyDto replyDto) {
    	// 게시글 조회
        Board board = boardRepository.findById(replyDto.getBoard_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        // 회원 조회
        Member member = memberRepository.findById(replyDto.getMember_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        // 부모 댓글 조회
        Reply parentReply = replyRepository.findById(replyDto.getParent_reply_no())
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
        // 대댓글 엔티티 생성
        Reply subReply = Reply.builder()
                .replyContent(replyDto.getReply_content())
                .replyStatus("N")
                .board(board)
                .member(member)
                .parentReply(parentReply)
                .regDate(LocalDateTime.now())
                .build();

        replyRepository.save(subReply); // 대댓글 저장
    }
    
    
    // 특정 게시글의 댓글 리스트 조회
    @Transactional(readOnly = true)
    public List<ReplyDto> getReplyListByBoard(Long boardNo) {
    	// 정상(N) 상태 댓글만 조회
        List<Reply> replies = replyRepository.findByBoard_BoardNoAndReplyStatus(boardNo, "N");

        Map<Long, ReplyDto> replyMap = new LinkedHashMap<>();
        List<ReplyDto> result = new ArrayList<>();

        for (Reply reply : replies) {
            ReplyDto dto = ReplyDto.toDto(reply);
            replyMap.put(dto.getReply_no(), dto);

            if (dto.getParent_reply_no() == null) {
            	// 부모 댓글이면 최상위 리스트에 추가
                result.add(dto);
            } else {
            	// 대댓글이면 부모 댓글의 하위 리스트에 추가
                ReplyDto parentDto = replyMap.get(dto.getParent_reply_no());
                if (parentDto != null) {
                    parentDto.getSubReplies().add(dto);
                    parentDto.setSubReplyCount(parentDto.getSubReplyCount() + 1); // 대댓글 수
                }
            }
        }

        return result;
    }
    
    
    // 댓글 수정
    @Transactional
    public ReplyDto updateReply(ReplyDto dto, Member member) {
        Reply reply = replyRepository.findById(dto.getReply_no())
            .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if (!reply.getMember().getMemberNo().equals(member.getMemberNo())) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        reply.setReplyContent(dto.getReply_content());
        reply.setModDate(LocalDateTime.now());
        replyRepository.save(reply);
        
        return ReplyDto.toDto(reply); // 기능 8
    }
    
    
    // 댓글 단일 조회(내용만)
    @Transactional(readOnly = true)
    public String getReplyContent(Long replyNo) {
        return replyRepository.findById(replyNo)
                              .map(Reply::getReplyContent)
                              .orElse(""); // 없으면 빈 문자열  반환
    }
    
    
    // 댓글 삭제
    @Transactional
    public void replyDelete(Long replyNo, Long memberNo) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        // 작성자 본인만 삭제 가능
        if (!Objects.equals(reply.getMember().getMemberNo(), memberNo)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }

        markReplyAsDeleted(reply); // 삭제 처리
    }

    // 댓글 및 대댓글 모두 삭제 상태로 변경하는 메소드(소프트 삭제)
    private void markReplyAsDeleted(Reply reply) {
        reply.setReplyStatus("Y");
        for (Reply child : reply.getChildReplies()) {
            markReplyAsDeleted(child); // 자식 대댓글까지 모두 삭제 처리
        }
    }
    
    
    // 계층형 구조로 댓글 반환
    @Transactional(readOnly = true)
    public List<ReplyDto> getHierarchicalRepliesByBoardNo(Long boardNo) {
        List<Reply> replies = replyRepository.findByBoard_BoardNoAndReplyStatus(boardNo, "N");
        List<ReplyDto> parentReplies = new ArrayList<>();
        Map<Long, List<ReplyDto>> subReplyMap = new HashMap<>();

        for (Reply reply : replies) {
            ReplyDto dto = ReplyDto.toDto(reply);

            if (dto.getParent_reply_no() == null) {
                parentReplies.add(dto);
            } else {
                subReplyMap.computeIfAbsent(dto.getParent_reply_no(), k -> new ArrayList<>()).add(dto);
            }
        }
        // 부모 댓글에 대댓글 매칭
        for (ReplyDto parent : parentReplies) {
            List<ReplyDto> subReplies = subReplyMap.get(parent.getReply_no());
            if (subReplies != null) {
                parent.setSubReplies(subReplies);
            }
        }

        return parentReplies;
    }
}
package com.mjc.groupware.reply.service;

import java.util.List;
import java.util.stream.Collectors;

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

    private final ReplyRepository replyRepository;  // 댓글 관련 DB 연동
    private final BoardRepository boardRepository;  // 게시글 관련 DB 연동
    private final MemberRepository memberRepository;  // 회원 관련 DB 연동

    // 댓글 작성
    public void createReply(ReplyDto replyDto) {
        // 댓글을 작성하려는 게시글을 조회
        Board board = boardRepository.findById(replyDto.getBoard_no())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 댓글 작성자의 회원 정보를 조회
        Member member = memberRepository.findById(replyDto.getMember_no())
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보를 찾을 수 없습니다."));

        // 댓글 엔티티 생성
        Reply reply = Reply.builder()
                .replyContent(replyDto.getReply_content())
                .replyStatus("N") 
                .board(board)
                .member(member)  // 댓글 작성자 설정
                .build();

        // 부모 댓글이 있을 경우 부모 댓글을 설정
        if (replyDto.getParent_reply_no() != null) {
            Reply parentReply = replyRepository.findById(replyDto.getParent_reply_no())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
            reply.setParentReply(parentReply);  // 부모 댓글 설정
        }

        // 댓글을 DB에 저장
        replyRepository.save(reply);
    }

    // 특정 댓글
    @Transactional(readOnly = true)
    public List<ReplyDto> getRepliesByBoardNo(Long boardNo) {
        // 게시글 번호와 댓글 상태가 'N'인 댓글들을 조회
        return replyRepository.findByBoard_BoardNoAndReplyStatus(boardNo, "N").stream()
                .map(ReplyDto::toDto)  // 댓글 엔티티를 DTO로 변환
                .collect(Collectors.toList());  // 리스트로 반환
    }

    // 게시글에 달린 계층 댓글
    @Transactional(readOnly = true)
    public List<ReplyDto> getHierarchicalRepliesByBoardNo(Long boardNo) {
        // 게시글에 달린 모든 댓글을 조회
        List<ReplyDto> allReplies = getRepliesByBoardNo(boardNo);
        
        // 부모 댓글들만 추출
        List<ReplyDto> parentReplies = allReplies.stream()
                .filter(reply -> reply.getParent_reply_no() == null)
                .collect(Collectors.toList());

        // 부모 댓글마다 자식 댓글을 찾아서 할당
        for (ReplyDto parent : parentReplies) {
            List<ReplyDto> children = allReplies.stream()
                    .filter(reply -> parent.getReply_no().equals(reply.getParent_reply_no()))
                    .collect(Collectors.toList());
            parent.setSubReplies(children);  // 자식 댓글을 부모 댓글에 추가
        }

        return parentReplies;  // 부모 댓글들을 반환
    }

    // 댓글 수정
    @Transactional
    public void updateReply(Long replyNo, Long memberNo, String newContent) {
        // 댓글을 조회
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 작성자 권한 검사
        if (!reply.getMember().getMemberNo().equals(memberNo)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 댓글 내용 수정
        reply.setReplyContent(newContent);
        
        // 수정된 댓글을 DB에 저장
        replyRepository.save(reply);
    }

    // 댓글 삭제
    @Transactional
    public void deleteReply(Long replyNo, Long memberNo) {
        // 댓글을 조회
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 작성자 권한 검사
        if (!reply.getMember().getMemberNo().equals(memberNo)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 댓글을 Soft Delete 처리 (상태를 'D'로 변경)
        reply.setReplyStatus("D");  // Soft delete
        replyRepository.save(reply);  // 변경된 댓글 상태를 DB에 저장
    }
}
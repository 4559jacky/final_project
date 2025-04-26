package com.mjc.groupware.reply.service;

import java.time.Duration;
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

 // 댓글 생성
    @Transactional
    public void replyCreate(ReplyDto replyDto) {
        Board board = boardRepository.findById(replyDto.getBoard_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        Member member = memberRepository.findById(replyDto.getMember_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        Reply reply = Reply.builder()
                .replyContent(replyDto.getReply_content())
                .replyStatus("N") // 기본 상태: 정상
                .board(board)
                .member(member)
                .regDate(LocalDateTime.now())
                .build();

        replyRepository.save(reply);
    }

    // 대댓글 생성
    @Transactional
    public void replyCreateSub(ReplyDto replyDto) {
        Board board = boardRepository.findById(replyDto.getBoard_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        Member member = memberRepository.findById(replyDto.getMember_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        Reply parentReply = replyRepository.findById(replyDto.getParent_reply_no())
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));

        Reply subReply = Reply.builder()
                .replyContent(replyDto.getReply_content())
                .replyStatus("N")
                .board(board)
                .member(member)
                .parentReply(parentReply)
                .regDate(LocalDateTime.now())
                .build();

        replyRepository.save(subReply);
    }

    // 댓글 트리 구조 반환
    @Transactional(readOnly = true)
    public List<ReplyDto> getRepliesByBoard(Long boardNo) {
        List<Reply> replies = replyRepository.findByBoard_BoardNoAndReplyStatus(boardNo, "N");

        Map<Long, ReplyDto> replyMap = new HashMap<>();
        List<ReplyDto> result = new ArrayList<>();

        for (Reply reply : replies) {
            ReplyDto dto = ReplyDto.toDto(reply);
            dto.setTimeAgo(getTimeAgo(dto.getReg_date()));

            replyMap.put(dto.getReply_no(), dto);

            if (dto.getParent_reply_no() == null) {
                result.add(dto);
            }
        }

        for (Reply reply : replies) {
            if (reply.getParentReply() != null) {
                ReplyDto parentDto = replyMap.get(reply.getParentReply().getReplyNo());
                if (parentDto != null) {
                    ReplyDto childDto = ReplyDto.toDto(reply);
                    childDto.setTimeAgo(getTimeAgo(childDto.getReg_date())); // 재귀 함수 사용
                    parentDto.getSubReplies().add(childDto);
                }
            }
        }

        return result;
    }
    
 // 댓글 수정
    @Transactional
    public void updateReply(ReplyDto replyDto, Member member) {
        Reply reply = replyRepository.findById(replyDto.getReply_no())
            .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        reply.setReplyContent(replyDto.getReply_content());
        reply.setModDate(LocalDateTime.now());
        replyRepository.save(reply);
    }

    

	/** 컨트롤러에 최신 내용을 돌려줄 때 사용 */
    @Transactional(readOnly = true)
    public String getReplyContent(Long replyNo) {
        return replyRepository.findById(replyNo)
                              .map(Reply::getReplyContent)
                              .orElse("");
    }
    

    // 댓글 삭제 (Soft delete)
    @Transactional
    public void replyDelete(Long replyNo, Long memberNo) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!reply.getMember().getMemberNo().equals(memberNo)) {
            throw new IllegalStateException("본인의 댓글만 삭제할 수 있습니다.");
        }

        reply.setReplyStatus("Y"); // 'Y' = 삭제됨
        reply.setModDate(LocalDateTime.now());

        replyRepository.save(reply);
    }
    

    // 계층형 구조로 댓글 반환
    @Transactional(readOnly = true)
    public List<ReplyDto> getHierarchicalRepliesByBoardNo(Long boardNo) {
        List<Reply> replies = replyRepository.findByBoard_BoardNoAndReplyStatus(boardNo, "N");
        List<ReplyDto> parentReplies = new ArrayList<>();
        Map<Long, List<ReplyDto>> subReplyMap = new HashMap<>();

        for (Reply reply : replies) {
            ReplyDto dto = ReplyDto.toDto(reply);
            dto.setTimeAgo(getTimeAgo(dto.getReg_date())); // 재귀 함수

            if (dto.getParent_reply_no() == null) {
                parentReplies.add(dto);
            } else {
                subReplyMap.computeIfAbsent(dto.getParent_reply_no(), k -> new ArrayList<>()).add(dto);
            }
        }

        for (ReplyDto parent : parentReplies) {
            List<ReplyDto> subReplies = subReplyMap.get(parent.getReply_no());
            if (subReplies != null) {
                parent.setSubReplies(subReplies);
            }
        }

        return parentReplies;
    }

    // 시간 경과 계산(사용자가 댓글 달았을때 시간 반영해줌)
    private String getTimeAgo(LocalDateTime time) {
        Duration duration = Duration.between(time, LocalDateTime.now());
        long minutes = duration.toMinutes();

        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        if (minutes < 1440) return (duration.toHours()) + "시간 전";
        return duration.toDays() + "일 전";
    }

}
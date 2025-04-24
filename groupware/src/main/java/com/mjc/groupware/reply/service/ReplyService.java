package com.mjc.groupware.reply.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(replyDto.getMember_no())
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보를 찾을 수 없습니다."));

        Reply reply = Reply.builder()
                .replyContent(replyDto.getReply_content())
                .replyStatus("N")
                .board(board)
                .member(member)
                .build();

        if (replyDto.getParent_reply_no() != null) {
            Reply parentReply = replyRepository.findById(replyDto.getParent_reply_no())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
            reply.setParentReply(parentReply);
        }

        replyRepository.save(reply);
    }

    // 대댓글 생성
    @Transactional
    public void replyCreateSub(ReplyDto replyDto) {
        replyCreate(replyDto);
    }
    
    @Transactional(readOnly = true)
    public List<ReplyDto> getRepliesByBoard(Long boardNo) {
        List<Reply> replies = replyRepository.findByBoard_BoardNoAndReplyStatus(boardNo, "N");

        // 대댓글 포함하여 트리 구조로 정리
        Map<Long, ReplyDto> replyMap = new HashMap<>();
        List<ReplyDto> result = new ArrayList<>();

        for (Reply reply : replies) {
            ReplyDto dto = ReplyDto.toDto(reply);
            dto.setTimeAgo(getTimeAgo(dto.getReg_date()));

            if (dto.getParent_reply_no() == null) {
                result.add(dto);
            }
            replyMap.put(dto.getReply_no(), dto);
        }

        for (Reply reply : replies) {
            if (reply.getParentReply() != null) {
                ReplyDto parentDto = replyMap.get(reply.getParentReply().getReplyNo());
                if (parentDto != null) {
                    parentDto.getSubReplies().add(ReplyDto.toDto(reply));
                }
            }
        }

        return result;
    }

    // 댓글 수정
    @Transactional
    public void replyUpdate(Long replyNo, Long memberNo, String newContent) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!reply.getMember().getMemberNo().equals(memberNo)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        reply.setReplyContent(newContent); // 내용 수정
        replyRepository.save(reply);  // 저장
    }

    // 댓글 삭제
    @Transactional
    public void replyDelete(Long replyNo, Long memberNo) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        
        // 댓글 작성자가 아닌 경우
        if (!reply.getMember().getMemberNo().equals(memberNo)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        reply.setReplyStatus("D");  // 댓글 상태를 'D'로 변경 (소프트 삭제)
        replyRepository.save(reply);  // 댓글 상태 변경 저장
    }
    
    
    // 댓글/대댓글 계층형 조회
    @Transactional(readOnly = true)
    public List<ReplyDto> getHierarchicalRepliesByBoardNo(Long boardNo) {
        // 게시글 번호(boardNo)와 댓글 상태("N" : 정상) 조건으로 댓글 리스트를 조회
        List<Reply> replies = replyRepository.findByBoard_BoardNoAndReplyStatus(boardNo, "N");

        // 부모 댓글들을 담을 리스트 생성
        List<ReplyDto> parentReplies = new ArrayList<>();
        // 부모 댓글 번호를 키로 대댓글 리스트를 저장할 맵 생성
        Map<Long, List<ReplyDto>> subReplyMap = new HashMap<>();

        // 댓글 리스트를 순회하며 부모 댓글과 대댓글을 구분하고 DTO로 변환
        for (Reply reply : replies) {
            ReplyDto dto = ReplyDto.toDto(reply);
            Long parentNo = dto.getParent_reply_no();

            if (parentNo == null) {
                // 부모 댓글
                parentReplies.add(dto);
            } else {
                // 대댓글
                subReplyMap.computeIfAbsent(parentNo, k -> new ArrayList<>()).add(dto);
            }
        }

        // 부모 댓글에 대댓글 연결
        for (ReplyDto parent : parentReplies) {
            List<ReplyDto> subReplies = subReplyMap.get(parent.getReply_no());
            if (subReplies != null) {
                parent.setSubReplies(subReplies);
            }
        }

        return parentReplies;
    }
    
    // 댓글 등록(작성자 => 몇시간전)
    private String getTimeAgo(LocalDateTime time) {
        Duration duration = Duration.between(time, LocalDateTime.now());
        long minutes = duration.toMinutes();
        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute(s) ago";
        } else if (minutes < 1440) {
            long hours = duration.toHours();
            return hours + " hour(s) ago";
        } else {
            long days = duration.toDays();
            return days + " day(s) ago";
        }
    }

	public Long getBoardNoByReply(Long replyNo) {
		Reply reply = replyRepository.findById(replyNo)
			.orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
		return reply.getBoard().getBoardNo();
	}
}
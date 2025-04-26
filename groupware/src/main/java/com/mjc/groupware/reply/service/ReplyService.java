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
    public List<ReplyDto> getReplyListByBoard(Long boardNo) {
        List<Reply> replies = replyRepository.findByBoard_BoardNoAndReplyStatus(boardNo, "N");

        // 대댓글을 트리 구조로 정리
        Map<Long, ReplyDto> replyMap = new LinkedHashMap<>();
        List<ReplyDto> result = new ArrayList<>();

        for (Reply reply : replies) {
            ReplyDto dto = ReplyDto.toDto(reply);
            replyMap.put(dto.getReply_no(), dto);

            if (dto.getParent_reply_no() == null) {
                result.add(dto); // 댓글
            } else {
                ReplyDto parentDto = replyMap.get(dto.getParent_reply_no());
                if (parentDto != null) {
                    parentDto.getSubReplies().add(dto); // 대댓글 추가
                    parentDto.setSubReplyCount(parentDto.getSubReplyCount() + 1); // 여기 추가!!
                }
            }
        }

        return result;
    }
    
    
    // 수정
    @Transactional
    public void updateReply(ReplyDto dto, Member member) {
        Reply reply = replyRepository.findById(dto.getReply_no())
            .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if (!reply.getMember().getMemberNo().equals(member.getMemberNo())) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        reply.setReplyContent(dto.getReply_content());
        reply.setModDate(LocalDateTime.now()); // 명시적 수정일자 설정 (optional)
    }
    

	/** 컨트롤러에 최신 내용을 돌려줄 때 사용 */
    @Transactional(readOnly = true)
    public String getReplyContent(Long replyNo) {
        return replyRepository.findById(replyNo)
                              .map(Reply::getReplyContent)
                              .orElse("");
    }
    

 // 현재 삭제 시 자식까지 삭제되는 CascadeType.ALL만 설정
 // 논리 삭제에서도 자식 댓글도 함께 처리하도록 하면 더 안전
    @Transactional
    public void replyDelete(Long replyNo, Long memberNo) {
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!Objects.equals(reply.getMember().getMemberNo(), memberNo)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }

        reply.setReplyStatus("Y");
    }

    

    // 계층형 구조로 댓글 반환
    @Transactional(readOnly = true)
    public List<ReplyDto> getHierarchicalRepliesByBoardNo(Long boardNo) {
        List<Reply> replies = replyRepository.findByBoard_BoardNoAndReplyStatus(boardNo, "N");
        List<ReplyDto> parentReplies = new ArrayList<>();
        Map<Long, List<ReplyDto>> subReplyMap = new HashMap<>();

        for (Reply reply : replies) {
            ReplyDto dto = ReplyDto.toDto(reply);
            //dto.setTimeAgo(getTimeAgo(dto.getReg_date())); // 재귀 함수

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

    

}
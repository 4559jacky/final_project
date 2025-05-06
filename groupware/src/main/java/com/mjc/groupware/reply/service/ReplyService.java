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
        // 댓글에 연결된 게시글이 존재하는지 확인
        Board board = boardRepository.findById(replyDto.getBoard_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        // 댓글 작성자가 존재하는지 확인
        Member member = memberRepository.findById(replyDto.getMember_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        // 댓글 객체 생성 및 저장
        Reply reply = Reply.builder()
                .replyContent(replyDto.getReply_content())
                .replyStatus("N")  // "N"은 댓글이 활성 상태임을 의미
                .board(board)
                .member(member)
                .regDate(LocalDateTime.now()) // 현재 시간을 댓글 등록 시간으로 설정
                .build();

        replyRepository.save(reply);  // 댓글 저장
    }
    
    
    // 대댓글 생성(부모 댓글 존재)
    @Transactional
    public void replyCreateSub(ReplyDto replyDto) {
        // 대댓글에 연결된 게시글이 존재하는지 확인
        Board board = boardRepository.findById(replyDto.getBoard_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        // 대댓글 작성자가 존재하는지 확인
        Member member = memberRepository.findById(replyDto.getMember_no())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        // 부모 댓글 존재 여부 확인
        Reply parentReply = replyRepository.findById(replyDto.getParent_reply_no())
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
        // 대댓글 객체 생성 및 저장
        Reply subReply = Reply.builder()
                .replyContent(replyDto.getReply_content())
                .replyStatus("N")  // "N"은 대댓글이 활성 상태임을 의미
                .board(board)
                .member(member)
                .parentReply(parentReply)  // 부모 댓글 설정
                .regDate(LocalDateTime.now()) // 현재 시간을 대댓글 등록 시간으로 설정
                .build();

        replyRepository.save(subReply);  // 대댓글 저장
    }
    
    
    // 특정 게시글의 댓글 리스트 조회
    @Transactional(readOnly = true)
    public List<ReplyDto> getReplyListByBoard(Long boardNo) {
        // 해당 게시글에 속한 활성 댓글들 조회
        List<Reply> replies = replyRepository.findByBoard_BoardNoAndReplyStatus(boardNo, "N");

        // 댓글들을 계층 구조로 변환하기 위해 맵을 생성
        Map<Long, ReplyDto> replyMap = new LinkedHashMap<>();
        List<ReplyDto> result = new ArrayList<>();

        for (Reply reply : replies) {
            // 댓글을 DTO로 변환
            ReplyDto dto = ReplyDto.toDto(reply);
            replyMap.put(dto.getReply_no(), dto);

            // 부모 댓글이면 최상위 리스트에 추가
            if (dto.getParent_reply_no() == null) {
                result.add(dto);
            } else {
                // 자식 댓글이면 부모 댓글에 추가
                ReplyDto parentDto = replyMap.get(dto.getParent_reply_no());
                if (parentDto != null) {
                    parentDto.getSubReplies().add(dto);
                    parentDto.setSubReplyCount(parentDto.getSubReplies().size()); // 대댓글 수 동기화
                }
            }
        }

        return result; // 계층 구조로 정리된 댓글 리스트 반환
    }
    
    
    // 댓글 수정
    @Transactional
    public ReplyDto replyUpdate(ReplyDto dto, Member member) {
        // 수정할 댓글 조회
        Reply reply = replyRepository.findById(dto.getReply_no())
            .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 댓글 작성자가 아니라면 수정할 수 없음
        if (!reply.getMember().getMemberNo().equals(member.getMemberNo())) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        // 댓글 내용 수정
        reply.setReplyContent(dto.getReply_content());
        reply.setModDate(LocalDateTime.now()); // 수정 시간 설정
        replyRepository.save(reply);  // 댓글 저장

        return ReplyDto.toDto(reply); // 수정된 댓글 DTO 반환
    }
    
    
    // 댓글 단일 조회(내용만)
    @Transactional(readOnly = true)
    public String getReplyContent(Long replyNo) {
        // 댓글이 존재하면 내용 반환, 없으면 빈 문자열 반환
        return replyRepository.findById(replyNo)
                              .map(Reply::getReplyContent)
                              .orElse("");
    }
    
    
    // 댓글 삭제
    @Transactional
    public void replyDelete(Long replyNo, Long memberNo) {
        // 삭제할 댓글 조회
        Reply reply = replyRepository.findById(replyNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        // 댓글 작성자가 아니라면 삭제할 수 없음
        if (!Objects.equals(reply.getMember().getMemberNo(), memberNo)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }

        markReplyAsDeleted(reply);  // 삭제 처리
    }
    

    // 댓글 및 대댓글 모두 삭제 상태로 변경
    private void markReplyAsDeleted(Reply reply) {
        reply.setReplyStatus("Y");  // "Y"는 삭제 상태를 의미
        for (Reply child : reply.getChildReplies()) {
            markReplyAsDeleted(child);  // 자식 댓글들도 재귀적으로 삭제 처리
        }
    }
    
    
    // 계층형 구조로 댓글 반환
    @Transactional(readOnly = true)
    public List<ReplyDto> getHierarchicalRepliesByBoardNo(Long boardNo) {
        // 게시글에 속한 댓글 조회
        List<Reply> replies = replyRepository.findByBoard_BoardNoAndReplyStatus(boardNo, "N");
        List<ReplyDto> parentReplies = new ArrayList<>();
        Map<Long, List<ReplyDto>> subReplyMap = new HashMap<>();

        for (Reply reply : replies) {
            // 댓글을 DTO로 변환
            ReplyDto dto = ReplyDto.toDto(reply);

            // 부모 댓글인지 확인하여 분류
            if (dto.getParent_reply_no() == null) {
                parentReplies.add(dto);  // 부모 댓글 리스트에 추가
            } else {
                // 대댓글은 subReplyMap에 저장
                subReplyMap.computeIfAbsent(dto.getParent_reply_no(), k -> new ArrayList<>()).add(dto);
            }
        }

        // 부모 댓글에 대댓글들을 연결
        for (ReplyDto parent : parentReplies) {
            List<ReplyDto> subReplies = subReplyMap.get(parent.getReply_no());
            if (subReplies != null) {
                parent.setSubReplies(subReplies);  // 대댓글 추가
                parent.setSubReplyCount(subReplies.size()); // 대댓글 수 동기화
            } else {
                parent.setSubReplyCount(0); // 대댓글이 없으면 0으로 설정
            }
        }

        return parentReplies;  // 부모 댓글들 반환
    }
}

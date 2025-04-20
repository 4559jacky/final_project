package com.mjc.groupware.reply.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.reply.dto.ReplyDto;
import com.mjc.groupware.reply.service.ReplyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    // 현재 로그인한 사용자 정보 가져오기
    private Member getLoginMember() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof com.mjc.groupware.member.security.MemberDetails)) {
            throw new IllegalStateException("로그인한 사용자 정보가 없습니다.");
        }
        throw new IllegalStateException("로그인한 사용자 정보가 없습니다.");
    }

    // 게시글 상세페이지 리다이렉트
    private String redirectToBoardDetail(Long boardNo) {
        return "redirect:/board/detail/" + boardNo;
    }

    // 댓글 작성
    @PostMapping("/replies/{boardNo}/create")
    public String createReply(@ModelAttribute ReplyDto replyDto, @PathVariable("boardNo") Long boardNo) {
        Member member = getLoginMember();
        replyDto.setMember_no(member.getMemberNo());
        replyDto.setBoard_no(boardNo);

        replyService.createReply(replyDto);
        return redirectToBoardDetail(boardNo);
    }

    // 대댓글 작성
    @PostMapping("/replies/{parentReplyNo}/createSubReply")
    public String createSubReply(@ModelAttribute ReplyDto replyDto,
                                 @PathVariable("parentReplyNo") Long parentReplyNo,
                                 @RequestParam("board_no") Long boardNo) {
        Member member = getLoginMember();
        replyDto.setMember_no(member.getMemberNo());
        replyDto.setParent_reply_no(parentReplyNo);
        replyDto.setBoard_no(boardNo);

        replyService.createReply(replyDto);
        return redirectToBoardDetail(boardNo);
    }

    // 댓글 수정
    @PostMapping("/replies/{replyNo}/update")
    public String updateReply(@ModelAttribute ReplyDto replyDto,
                              @PathVariable("replyNo") Long replyNo) {
        Member member = getLoginMember();
        replyService.updateReply(replyNo, member.getMemberNo(), replyDto.getReply_content());
        return redirectToBoardDetail(replyDto.getBoard_no());
    }

    // 댓글 삭제
    @PostMapping("/replies/{replyNo}/delete")
    public String deleteReply(@PathVariable("replyNo") Long replyNo,
                              @RequestParam("board_no") Long boardNo) {
        Member member = getLoginMember();
        replyService.deleteReply(replyNo, member.getMemberNo());
        return redirectToBoardDetail(boardNo);
    }
}
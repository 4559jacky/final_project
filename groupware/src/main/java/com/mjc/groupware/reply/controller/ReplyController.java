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

    // 로그인 하지 않으면 예외 발생
    private Member getLoginMember() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof com.mjc.groupware.member.security.MemberDetails) {
            return ((com.mjc.groupware.member.security.MemberDetails) principal).getMember();
        }
        throw new IllegalStateException("로그인한 사용자 정보가 없습니다.");
    }

    // 게시글 상세 페이지로 리다이렉트
    private String redirectToBoardDetail(Long boardNo) {
        return "redirect:/board/detail/" + boardNo;
    }

   
    //댓글 생성 서비스 호출 후 게시글 상세 페이지로 리다이렉트
    @PostMapping("/replies/{boardNo}/create")
    public String replyCreate(@ModelAttribute ReplyDto replyDto, @PathVariable("boardNo") Long boardNo) {
        Member member = getLoginMember();  // 로그인 사용자 정보 조회
        replyDto.setMember_no(member.getMemberNo());  // 댓글 작성자 설정
        replyDto.setBoard_no(boardNo);  // 댓글이 달릴 게시글 번호 설정

        replyService.replyCreate(replyDto);  // 댓글 생성 서비스 호출
        return redirectToBoardDetail(boardNo);  // 상세 페이지로 리다이렉트
    }

     
     //대댓글 생성 서비스 호출 후 상세 페이지
    @PostMapping("/replies/{boardNo}/{parentReplyNo}/create-sub")
    public String replyCreateSub(@ModelAttribute ReplyDto replyDto,
                                 @PathVariable("boardNo") Long boardNo,
                                 @PathVariable("parentReplyNo") Long parentReplyNo) {
        Member member = getLoginMember();  // 로그인 사용자 정보 조회
        replyDto.setMember_no(member.getMemberNo());  // 대댓글 작성자 설정
        replyDto.setBoard_no(boardNo);  // 게시글 번호 설정
        replyDto.setParent_reply_no(parentReplyNo);  // 부모 댓글 번호 설정

        replyService.replyCreateSub(replyDto);  // 대댓글 생성 서비스 호출
        return redirectToBoardDetail(boardNo);  // 상세 페이지로 리다이렉트
    }

    
     // 댓글 수정
    @PostMapping("/replies/{replyNo}/update")
    public String replyUpdate(@ModelAttribute ReplyDto replyDto,
                              @PathVariable("replyNo") Long replyNo) {
        Member member = getLoginMember();  // 로그인 사용자 정보 조회
        replyService.replyUpdate(replyNo, member.getMemberNo(), replyDto.getReply_content());  // 댓글 수정 서비스 호출
        return redirectToBoardDetail(replyDto.getBoard_no());  // 상세 페이지로 리다이렉트
    }
    

    // 댓글 삭제
    @PostMapping("/replies/{replyNo}/delete")
    public String replyDelete(@PathVariable("replyNo") Long replyNo,
                              @RequestParam("board_no") Long boardNo) {
        Member member = getLoginMember();  // 로그인 사용자 정보 조회
        replyService.replyDelete(replyNo, member.getMemberNo());  // 댓글 삭제 서비스 호출
        return redirectToBoardDetail(boardNo);  // 상세 페이지로 리다이렉트
    }
}
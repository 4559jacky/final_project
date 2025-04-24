package com.mjc.groupware.reply.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    // 댓글 생성 서비스 호출 후 게시글 상세 페이지로 리다이렉트
    @PostMapping("/replies/{boardNo}/create")
    public String replyCreate(@ModelAttribute ReplyDto replyDto, @PathVariable("boardNo") Long boardNo) {
        Member member = getLoginMember();  // 로그인 사용자 정보 조회
        replyDto.setMember_no(member.getMemberNo());  // 댓글 작성자 설정
        replyDto.setBoard_no(boardNo);  // 댓글이 달릴 게시글 번호 설정

        replyService.replyCreate(replyDto);  // 댓글 생성 서비스 호출
        return redirectToBoardDetail(boardNo);  // 상세 페이지로 리다이렉트
    }

    // 대댓글 생성 서비스 호출 후 상세 페이지
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
    public String replyUpdate(@RequestParam("reply_content") String replyContent,
                              @PathVariable("replyNo") Long replyNo) {
        Member member = getLoginMember();  // 로그인 사용자 정보 조회
        replyService.replyUpdate(replyNo, member.getMemberNo(), replyContent);  // 댓글 수정 서비스 호출
        return redirectToBoardDetail(replyService.getBoardNoByReply(replyNo));  // 상세 페이지로 리다이렉트
    }
    // 댓글 삭제
    @DeleteMapping("/replies/{replyNo}/delete")
    @ResponseBody
    public Map<String, String> replyDelete(@PathVariable("replyNo") Long replyNo,
                                           @AuthenticationPrincipal Member loginUser) {
        Map<String, String> result = new HashMap<>();

        try {
            replyService.replyDelete(replyNo, loginUser.getMemberNo());
            result.put("res_code", "200");
            result.put("res_msg", "댓글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            result.put("res_code", "500");
            result.put("res_msg", "댓글 삭제 중 오류 발생");
        }

        return result;
    }
}
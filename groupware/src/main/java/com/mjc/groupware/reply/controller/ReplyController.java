package com.mjc.groupware.reply.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.reply.dto.ReplyDto;
import com.mjc.groupware.reply.service.ReplyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    // 현재 로그인한 회원 정보(Member)를 가져오는 메소드
    private Member getLoginMember(MemberDetails memberDetails) {
        if (memberDetails == null) {
            throw new IllegalStateException("로그인한 사용자 정보가 없습니다.");
        }
        return memberDetails.getMember();
    }
    
    
    // 게시글 상세보기 화면으로 리다이렉트
    private String redirectToBoardDetail(Long boardNo) {
        return "redirect:/board/detail/" + boardNo;
    }
    
    
    //댓글 작성 처리
    @PostMapping("/replies/{boardNo}/create")
    public String replyCreate(@ModelAttribute ReplyDto replyDto, @PathVariable("boardNo") Long boardNo, @AuthenticationPrincipal MemberDetails memberDetails) {
        Member member = getLoginMember(memberDetails);
        replyDto.setMember_no(member.getMemberNo()); // 작성자 번호 설정
        replyDto.setBoard_no(boardNo); // 게시글 번호 설정
        replyDto.setDeptName(member.getDept() != null ? member.getDept().getDeptName() : "부서없음");

        replyService.replyCreate(replyDto); // 댓글 동록 호출
        return redirectToBoardDetail(boardNo); // 게시글 상세보기로 이동
    }
    
    
    // 대댓글(답글) 작성 처리
    @PostMapping("/replies/{boardNo}/{parentReplyNo}/create-sub")
    public String replyCreateSub(@ModelAttribute ReplyDto replyDto,
                                @PathVariable("boardNo") Long boardNo,
                                @PathVariable("parentReplyNo") Long parentReplyNo,
                                @AuthenticationPrincipal MemberDetails memberDetails) {
        Member member = getLoginMember(memberDetails);
        replyDto.setMember_no(member.getMemberNo()); // 작성자 번호
        replyDto.setBoard_no(boardNo); // 게시글 번호
        replyDto.setParent_reply_no(parentReplyNo); // 부모 댓글 번호
        replyDto.setDeptName(member.getDept() != null ? member.getDept().getDeptName() : "부서없음");

        replyService.replyCreateSub(replyDto); // 대댓글 등록 호출
        return redirectToBoardDetail(boardNo); // 게시글 상세보기로 이동
    }
    
    
    // 댓글 수정
    @PostMapping("/replies/{replyNo}/update")
    @ResponseBody
    public Map<String, Object> updateReplyAjax(@PathVariable("replyNo") Long replyNo,
                                              @RequestBody Map<String, String> payload,
                                              @AuthenticationPrincipal MemberDetails memberDetails) {
        String newContent = payload.get("reply_content"); // 새로운 댓글 내용
        ReplyDto replyDto = new ReplyDto();
        replyDto.setReply_no(replyNo);
        replyDto.setReply_content(newContent);

        ReplyDto updatedDto = replyService.updateReply(replyDto, getLoginMember(memberDetails));
        
        // 결과를 맵에 담아서 반환
        Map<String, Object> result = new HashMap<>();
        result.put("success", true); // 성공 여부
        result.put("modDateFormatted", updatedDto.getModDateFormatted() != null ? updatedDto.getModDateFormatted() : updatedDto.getRegDateFormatted()); // 기능 8
        result.put("isModified", updatedDto.isModified()); // 수정 여부
        return result;
    }
    
    
    // 댓글 삭제
    @PostMapping("/replies/{replyNo}/delete")
    @ResponseBody
    public Map<String, String> replyDelete(@PathVariable("replyNo") Long replyNo,
                                          @AuthenticationPrincipal MemberDetails memberDetails) {
        Map<String, String> result = new HashMap<>();
        try {
            Member member = getLoginMember(memberDetails);
            replyService.replyDelete(replyNo, member.getMemberNo()); // 댓글 삭제 처리
            result.put("res_code", "200");
            result.put("res_msg", "댓글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            result.put("res_code", "500");
            result.put("res_msg", "댓글 삭제 중 오류 발생");
        }
        return result;
    }
}
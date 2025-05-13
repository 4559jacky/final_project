package com.mjc.groupware.reply.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.reply.dto.ReplyDto;
import com.mjc.groupware.reply.entity.Reply;
import com.mjc.groupware.reply.repository.ReplyRepository;
import com.mjc.groupware.reply.service.ReplyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;  // 댓글 관련 서비스 클래스
    private final ReplyRepository replyRepository;  // 댓글 관련 데이터베이스 접근을 위한 레포지토리

    // 로그인된 사용자 정보를 얻는 메소드
    private Member getLoginMember(MemberDetails memberDetails) {
        if (memberDetails == null) {
            throw new IllegalStateException("로그인한 사용자 정보가 없습니다.");
        }
        return memberDetails.getMember();  // MemberDetails 객체에서 회원 정보를 반환
    }
    
    
    // 게시글 상세 페이지로 리다이렉트하는 메소드
    private String redirectToBoardDetail(Long boardNo) {
        return "redirect:/board/detail/" + boardNo;
    }
    
    
    // 댓글 등록 API (게시글에 댓글을 달기 위한 POST 요청)
    @PostMapping("/replies/{boardNo}/create")
    public String replyCreate(@ModelAttribute ReplyDto replyDto, @PathVariable("boardNo") Long boardNo, @AuthenticationPrincipal MemberDetails memberDetails) {
        Member member = getLoginMember(memberDetails);  // 로그인된 사용자 정보 얻기
        replyDto.setMember_no(member.getMemberNo());  // 댓글 작성자 회원 번호 설정
        replyDto.setBoard_no(boardNo);  // 댓글이 달릴 게시글 번호 설정
        replyDto.setDeptName(member.getDept() != null ? member.getDept().getDeptName() : "부서없음");  // 댓글 작성자의 부서 정보 설정

        replyService.replyCreate(replyDto);  // 댓글 생성 서비스 호출
        return redirectToBoardDetail(boardNo);  // 댓글이 작성된 후 게시글 상세 페이지로 리다이렉트
    }
    
    
    // 대댓글 등록 API (댓글에 대한 대댓글을 달기 위한 POST 요청)
    @PostMapping("/replies/{boardNo}/{parentReplyNo}/create-sub")
    public String replyCreateSub(@ModelAttribute ReplyDto replyDto,
                                 @PathVariable("boardNo") Long boardNo,
                                 @PathVariable("parentReplyNo") Long parentReplyNo,
                                 @AuthenticationPrincipal MemberDetails memberDetails) {
        Member member = getLoginMember(memberDetails);  // 로그인된 사용자 정보 얻기
        replyDto.setMember_no(member.getMemberNo());  // 대댓글 작성자 회원 번호 설정
        replyDto.setBoard_no(boardNo);  // 대댓글이 달릴 게시글 번호 설정
        replyDto.setParent_reply_no(parentReplyNo);  // 대댓글의 부모 댓글 번호 설정
        replyDto.setDeptName(member.getDept() != null ? member.getDept().getDeptName() : "부서없음");  // 댓글 작성자의 부서 정보 설정

        replyService.replyCreateSub(replyDto);  // 대댓글 생성 서비스 호출
        return redirectToBoardDetail(boardNo);  // 대댓글 작성 후 게시글 상세 페이지로 리다이렉트
    }
    
    
    // 댓글 수정 API (댓글 내용 수정)
    @PostMapping("/replies/{replyNo}/update")
    @ResponseBody
    public Map<String, Object> replyUpdate(@PathVariable("replyNo") Long replyNo,
                                           @RequestBody Map<String, String> payload,
                                           @AuthenticationPrincipal MemberDetails memberDetails) {
        String newContent = payload.get("reply_content");  // 수정된 댓글 내용 받기
        ReplyDto replyDto = new ReplyDto();
        replyDto.setReply_no(replyNo);  // 수정할 댓글 번호 설정
        replyDto.setReply_content(newContent);  // 수정된 댓글 내용 설정

        ReplyDto updatedDto = replyService.replyUpdate(replyDto, getLoginMember(memberDetails));  // 댓글 수정 서비스 호출
        
        // 수정된 댓글 정보 반환
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        String modDate = updatedDto.getModDateFormatted() != null ? 
                         updatedDto.getModDateFormatted() + " (수정됨)" : 
                         updatedDto.getRegDateFormatted();  // 수정된 날짜 또는 등록된 날짜 반환
        result.put("modDateFormatted", modDate);
        return result;  // JSON 형식으로 응답
    }
    
    
    // 댓글 삭제 API (댓글 삭제)
    @PostMapping("/replies/{replyNo}/delete")
    @ResponseBody
    public Map<String, String> replyDelete(@PathVariable("replyNo") Long replyNo,
                                           @AuthenticationPrincipal MemberDetails memberDetails) {
        Map<String, String> result = new HashMap<>();
        try {
            Member member = getLoginMember(memberDetails);  // 로그인된 사용자 정보 얻기
            replyService.replyDelete(replyNo, member.getMemberNo());  // 댓글 삭제 서비스 호출
            result.put("res_code", "200");
            result.put("res_msg", "댓글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            result.put("res_code", "500");
            result.put("res_msg", "댓글 삭제 중 오류 발생");
        }
        return result;  // 삭제 결과를 JSON 형식으로 응답
    }
    
    
    // 새 API: 특정 댓글의 대댓글 수 조회
    @GetMapping("/replies/count/{replyNo}")
    @ResponseBody
    public Map<String, Integer> getSubReplyCount(@PathVariable("replyNo") Long replyNo) {
        Map<String, Integer> result = new HashMap<>();
        int count = replyRepository.findByParentReply_ReplyNoAndReplyStatus(replyNo, "N").size();  // 해당 댓글의 대댓글 수 조회
        result.put("count", count);  // 대댓글 수를 결과로 반환
        return result;  // JSON 형식으로 대댓글 수 반환
    }
    
    // 댓글에서 +더보기 버튼 추가 코드
    @GetMapping("/replies/{boardNo}")
    @ResponseBody
    public Map<String, Object> getRepliesByBoardPaged(
            @PathVariable("boardNo") Long boardNo,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {

        // 부모 댓글만 페이징 조회 (상위 댓글)
        Page<Reply> replyPage = replyRepository.findByBoard_BoardNoAndParentReplyIsNullAndReplyStatus(
                boardNo, "N", PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regDate"))
        );

        // 각 댓글에 대해 대댓글 포함하는 DTO 리스트 생성
        List<ReplyDto> result = replyPage.getContent().stream()
                .map(reply -> {
                    ReplyDto dto = ReplyDto.toDto(reply);

                    // 대댓글 조회 및 DTO 변환
                    List<Reply> children = replyRepository.findByParentReply_ReplyNoAndReplyStatus(reply.getReplyNo(), "N");
                    dto.setSubReplies(children.stream().map(ReplyDto::toDto).toList());
                    dto.setSubReplyCount(children.size());

                    return dto;
                }).toList();

        // 응답 구성
        Map<String, Object> map = new HashMap<>();
        map.put("replies", result);
        map.put("hasMore", replyPage.hasNext()); // 다음 페이지 존재 여부
        map.put("currentPage", page); // (선택) 현재 페이지 디버깅용

        return map;
    }
}

package com.mjc.groupware.vote.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.mjc.groupware.common.annotation.CheckPermission;
import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.vote.dto.VoteCreateRequest;
import com.mjc.groupware.vote.dto.VoteDto;
import com.mjc.groupware.vote.service.VoteService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    // 투표 생성
    @CheckPermission("BOARD_CRU")
    @PostMapping("/vote")
    @ResponseBody
    public ResponseEntity<Long> createVote(@RequestBody VoteCreateRequest req) {
        Long id = voteService.createVote(req.getVoteDto(), req.getOptions());
        return ResponseEntity.ok(id);
    }
    
    
    // 특정 투표 조회
    @CheckPermission("BOARD_R")
    @GetMapping("/vote/{voteNo}")
    @ResponseBody
    public ResponseEntity<VoteDto> getVote(@PathVariable("voteNo") Long voteNo, HttpServletRequest request) {
    	// URL 직접 접근을 차단 :: Ajax 요청이 아니면 차단
    	String header = request.getHeader("X-Custom-Ajax");
    	if (!"true".equals(header)) {
    		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "허용되지 않은 접근입니다.");
    	}
    	
        return voteService.getVote(voteNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    
    // 전체 투표 목록 조회
    @CheckPermission("BOARD_R")
    @GetMapping("/vote")
    @ResponseBody
    public ResponseEntity<List<VoteDto>> getAllVotes(HttpServletRequest request) {
    	// URL 직접 접근을 차단 :: Ajax 요청이 아니면 차단
    	String header = request.getHeader("X-Custom-Ajax");
    	if (!"true".equals(header)) {
    		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "허용되지 않은 접근입니다.");
    	}
    	
        return ResponseEntity.ok(voteService.getAllVotes());
    }
    
    
    // 투표 수정
    @CheckPermission("BOARD_CRU")
    @PutMapping("/vote/{voteNo}")
    @ResponseBody
    public ResponseEntity<String> updateVote(
            @PathVariable("voteNo") Long voteNo,
            @RequestBody VoteCreateRequest req) {
        voteService.updateVote(voteNo, req.getVoteDto(), req.getOptions());
        return ResponseEntity.ok("updated");
    }
    
    
    // 투표 삭제(투표 번호 기준)
    @CheckPermission("BOARD_CRU")
    @DeleteMapping("/vote/{voteNo}")
    @ResponseBody
    public ResponseEntity<String> deleteVote(@PathVariable("voteNo") Long voteNo) {
        voteService.deleteVoteByBoardNo(voteNo);
        return ResponseEntity.ok("deleted");
    }
    

    // 투표 참여(투표 제출) - 오류창이 여기서 발생(수정) - 정상작동 완료 / 투표하기(수정완료)
    //@AuthenticationPrincipal MemberDetails memberDetails 추가
    @CheckPermission("BOARD_R")
    @PostMapping("/vote/{voteNo}/submit")
    @ResponseBody
    public ResponseEntity<String> submitVote(
            @PathVariable("voteNo") Long voteNo,
            @RequestParam("optionNos") List<Long> optionNos,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        if (memberDetails == null || memberDetails.getMember() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Long memberNo = memberDetails.getMember().getMemberNo();
        voteService.participate(voteNo, optionNos, memberNo);
        return ResponseEntity.ok("투표 성공");
    }
    

    // 게시글 번호로 투표 삭제
    @CheckPermission("BOARD_CRU")
    @DeleteMapping("/vote/delete/by-board/{boardNo}")
    @ResponseBody
    public ResponseEntity<String> deleteVoteByBoard(@PathVariable("boardNo") Long boardNo) {
        boolean deleted = voteService.deleteVoteByBoardNo(boardNo);
        if (deleted) {
            return ResponseEntity.ok("투표 삭제 완료");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제할 투표가 없습니다.");
        }
    }
    
    
    // 투표 결과 조회
    @CheckPermission("BOARD_R")
    @GetMapping("/vote/{voteNo}/result")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getVoteResult(@PathVariable("voteNo") Long voteNo) {
        List<Map<String, Object>> result = voteService.getVoteResultForChart(voteNo);
        return ResponseEntity.ok(result);
    }
    

    // 중복 투표 여부 체크
    @GetMapping("/vote/{voteNo}/has-voted")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> hasVoted(
            @PathVariable("voteNo") Long voteNo,
            @RequestParam("memberNo") Long memberNo,
            HttpServletRequest request) {
    	// URL 직접 접근을 차단 :: Ajax 요청이 아니면 차단
    	String header = request.getHeader("X-Custom-Ajax");
    	if (!"true".equals(header)) {
    		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "허용되지 않은 접근입니다.");
    	}
    	
        boolean voted = voteService.hasUserAlreadyVoted(voteNo, memberNo);
        return ResponseEntity.ok(Map.of("voted", voted));
    }
    

    // 마감 여부 확인
    @GetMapping("/vote/{voteNo}/is-closed")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> isVoteClosed(@PathVariable("voteNo") Long voteNo) {
        boolean closed = voteService.isVoteClosed(voteNo);
        return ResponseEntity.ok(Map.of("closed", closed));
    }
    
    
    
    @GetMapping("/vote/{voteNo}/should-alert")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> alertIfVoteClosed(
            @PathVariable("voteNo") Long voteNo,
            @RequestParam("memberNo") Long memberNo) {
        try {
            if (voteService.isVoteClosed(voteNo)) {
                voteService.closeVoteAndNotify(voteNo);
                return ResponseEntity.ok(Map.of(
                    "shouldAlert", true,
                    "title", "투표 마감 알림",
                    "message", "투표가 마감되었습니다.",
                    "senderName", "시스템",
                    "boardNo", 0L // 적절한 값으로 교체
                ));
            }
            return ResponseEntity.ok(Map.of("shouldAlert", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("shouldAlert", false));
        }
    }
    
}

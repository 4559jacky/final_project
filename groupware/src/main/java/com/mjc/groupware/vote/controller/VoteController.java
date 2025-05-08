package com.mjc.groupware.vote.controller;

import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.vote.dto.VoteCreateRequest;
import com.mjc.groupware.vote.dto.VoteDto;
import com.mjc.groupware.vote.service.VoteService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    // 투표 생성
    @PostMapping("/vote")
    @ResponseBody
    public ResponseEntity<Long> createVote(@RequestBody VoteCreateRequest req) {
        Long id = voteService.createVote(req.getVoteDto(), req.getOptions());
        return ResponseEntity.ok(id);
    }
    
    // 특정 투표 조회
    @GetMapping("/vote/{voteNo}")
    @ResponseBody
    public ResponseEntity<VoteDto> getVote(@PathVariable("voteNo") Long voteNo) {
        return voteService.getVote(voteNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 전체 투표 목록 조회
    @GetMapping("/vote")
    @ResponseBody
    public ResponseEntity<List<VoteDto>> getAllVotes() {
        return ResponseEntity.ok(voteService.getAllVotes());
    }
    
    // 투표 수정
    @PutMapping("/vote/{voteNo}")
    @ResponseBody
    public ResponseEntity<String> updateVote(
            @PathVariable("voteNo") Long voteNo,
            @RequestBody VoteDto dto) {
        voteService.updateVote(voteNo, dto);
        return ResponseEntity.ok("updated");
    }
    
    // 투표 삭제(투표 번호 기준)
    @DeleteMapping("/vote/{voteNo}")
    @ResponseBody
    public ResponseEntity<String> deleteVote(@PathVariable("voteNo") Long voteNo) {
        voteService.deleteVoteByBoardNo(voteNo);
        return ResponseEntity.ok("deleted");
    }
    
    // 투표 참여(투표 제출)
    @PostMapping("/vote/{voteNo}/submit")
    public ResponseEntity<?> submitVote(
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
            @RequestParam("memberNo") Long memberNo) {
        boolean voted = voteService.hasUserAlreadyVoted(voteNo, memberNo);
        return ResponseEntity.ok(Map.of("voted", voted));
    }

    // 마감 여부 확인
    @GetMapping("/vote/{voteNo}/is-closed")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> isVoteClosed(@PathVariable("voteNo") Long voteNo) {
        System.out.println("🟡 isVoteClosed API 호출됨 - voteNo: " + voteNo);

        boolean closed = voteService.isVoteClosed(voteNo);
        System.out.println("⏰ 마감 여부: " + closed);

        if (closed) {
            voteService.notifyVoteClosed(voteNo); // 🎯 마감 시 알림 전송
        }

        return ResponseEntity.ok(Map.of("closed", closed));
    }

    
}

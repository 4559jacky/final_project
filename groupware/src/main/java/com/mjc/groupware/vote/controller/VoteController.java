package com.mjc.groupware.vote.controller;

import com.mjc.groupware.vote.dto.VoteCreateRequest;
import com.mjc.groupware.vote.dto.VoteDto;
import com.mjc.groupware.vote.service.VoteService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @ResponseBody
    public ResponseEntity<String> submitVote(
            @PathVariable("voteNo") Long voteNo,
            @RequestParam("optionNos") List<Long> optionNos,
            @RequestParam("memberNo") Long memberNo) {
        voteService.participate(voteNo, optionNos, memberNo);
        return ResponseEntity.ok("submitted");
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
    
 // 투표 결과 조회 (Chart.js 용 데이터)
    @GetMapping("/vote/{voteNo}/result")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getVoteResult(@PathVariable("voteNo") Long voteNo) {
        List<Map<String, Object>> result = voteService.getVoteResultForChart(voteNo);
        return ResponseEntity.ok(result);
    }
}

package com.mjc.groupware.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.dto.PageDto;
import com.mjc.groupware.board.dto.SearchDto;
import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.entity.BoardAttach;
import com.mjc.groupware.board.service.BoardAttachService;
import com.mjc.groupware.board.service.BoardService;
import com.mjc.groupware.common.annotation.CheckPermission;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.reply.dto.ReplyDto;
import com.mjc.groupware.reply.service.ReplyService;
import com.mjc.groupware.vote.dto.VoteCreateRequest;
import com.mjc.groupware.vote.repository.VoteRepository;
import com.mjc.groupware.vote.service.VoteService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class BoardController {

    private final Logger logger = LoggerFactory.getLogger(BoardController.class);
    private final BoardService boardService;
    private final BoardAttachService boardAttachService;
    private final ReplyService replyService;
    private final VoteRepository voteRepository;
    
    // 알람 2025-05-14(수요일)
    private final VoteService voteService;
    
    @CheckPermission("BOARD_CRU")
    @GetMapping("/board/create")
    public String createBoardView(Model model) {
        model.addAttribute("boardDto", new BoardDto());
        return "board/create";
    }
    
    // @AuthenticationPrincipal(expression = "member") Member loginMember) = 첨부파일이 안되서 다시 추가
    @CheckPermission("BOARD_CRU")
    @PostMapping("/board")
    @ResponseBody
    public Map<String, String> createBoard(@ModelAttribute BoardDto dto,
            							   @RequestParam(value = "files", required = false) List<MultipartFile> files,
            							   @RequestParam(value = "vote_json", required = false) String voteJson,
            							   @AuthenticationPrincipal(expression = "member") Member loginMember) {
    			Map<String, String> resultMap = new HashMap<>();
    			resultMap.put("res_code", "500");
    			resultMap.put("res_msg", "게시글 등록 중 오류가 발생하였습니다.");

    				try {
    					dto.setMember_no(loginMember.getMemberNo()); // ✅ 서버에서 직접 설정

    					ObjectMapper mapper = new ObjectMapper();
    					mapper.registerModule(new JavaTimeModule());

    					VoteCreateRequest voteRequest = null;
    					if (voteJson != null) {
    						String trimmed = voteJson.trim();
    						if (!trimmed.isEmpty() && (trimmed.startsWith("{") || trimmed.startsWith("["))) {
    							voteRequest = mapper.readValue(trimmed, VoteCreateRequest.class);
    						}
    					}

    					boardService.createBoard(dto, files, voteRequest);
    					resultMap.put("res_code", "200");
    					resultMap.put("res_msg", "게시글이 등록되었습니다.");
    				} catch (Exception e) {
    					e.printStackTrace();
    					resultMap.put("res_msg", e.getMessage());
    				}
    				return resultMap;
    }
    
    @CheckPermission("BOARD_R")
    @GetMapping("/board/list")
    public String selectBoardAll(Model model, SearchDto searchDto, PageDto pageDto) {
        if (pageDto.getNowPage() == 0) pageDto.setNowPage(1);
        if (pageDto.getNumPerPage() == 0) pageDto.setNumPerPage(10);

        List<Board> fixedList = boardService.selectFixedBoardList();
        model.addAttribute("fixedList", fixedList);

        Page<Board> boardList = boardService.selectBoardAll(searchDto, pageDto);
        pageDto.setTotalPage(boardList.getTotalPages());
        pageDto.setTotalCount((int) boardList.getTotalElements());

        model.addAttribute("boardList", boardList.getContent());
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("pageDto", pageDto);

        return "board/list";
    }
    
    @CheckPermission("BOARD_R")
    @GetMapping("/board/detail/{boardNo}")
    public String selectBoardOne(@PathVariable("boardNo") Long boardNo, Model model) {
        boardService.updateViews(boardNo);

        Optional<Board> optionalBoard = boardService.selectBoardOne(boardNo);
        if (!optionalBoard.isPresent()) {
            model.addAttribute("error", "해당 게시글을 찾을 수 없습니다.");
            return "error";
        }

        Board board = optionalBoard.get();
        model.addAttribute("board", board);
        model.addAttribute("attachList", boardAttachService.selectAttachList(boardNo));

        List<ReplyDto> initialReplies = replyService.getRepliesByBoardPaged(boardNo, 0, 5);
        model.addAttribute("replyList", initialReplies);
        model.addAttribute("hasMoreReplies", initialReplies.size() == 5);

        // ✅ 투표가 있을 경우 마감 여부만 체크 (알림 전송은 제외)
        if (board.getVote() != null) {
            model.addAttribute("vote", board.getVote());
            model.addAttribute("voteOptions", board.getVote().getVoteOptions());

            boolean isVoteClosed = board.getVote().getEndDate()
                .isBefore(java.time.LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul")));
            model.addAttribute("isVoteClosed", isVoteClosed);
        }

        return "board/detail";
    }
    
    @CheckPermission("BOARD_CRU")
    @GetMapping("/board/{id}/update")
    public String updateBoardView(@PathVariable("id") Long id, Model model) {
        Optional<Board> optionalBoard = boardService.selectBoardOne(id);
        Long memberNo = optionalBoard.get().getMember().getMemberNo();
        
        // 본인이 아닌데 URL 을 바꿔서 진입하려고 하면 Security에 의해 차단해야 함
     	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
     	
     	if (authentication == null || !authentication.isAuthenticated()) {
     		throw new AccessDeniedException("로그인 정보가 없습니다.");
     	}
     		
     	MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
     	Long currentMemberNo = memberDetails.getMember().getMemberNo();
     	
     	if (!memberNo.equals(currentMemberNo)) {
     		throw new AccessDeniedException("접근 권한이 없습니다.");
     	}
        
        if (optionalBoard.isPresent()) {
            model.addAttribute("board", optionalBoard.get());
            List<BoardAttach> attachList = boardAttachService.selectAttachList(id);
            model.addAttribute("attachList", attachList);
            return "board/update";
        } else {
            model.addAttribute("error", "해당 게시글을 찾을 수 없습니다.");
            return "error";
        }
    }
    
    @CheckPermission("BOARD_CRU")
    @PostMapping("/board/{boardNo}/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateBoard(@PathVariable("boardNo") Long boardNo,
                                                           @ModelAttribute BoardDto boardDto,
                                                           @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                                           @RequestParam(value = "deleteFiles", required = false) List<Long> deleteFiles,
                                                           @AuthenticationPrincipal(expression = "member") Member loginMember) {
        Map<String, String> result = new HashMap<>();
        try {
        	Optional<Board> optionalBoard = boardService.selectBoardOne(boardNo);
            Long memberNo = optionalBoard.get().getMember().getMemberNo();
        	
        	// 본인이 아닌데 URL 을 바꿔서 진입하려고 하면 Security에 의해 차단해야 함
         	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         	
         	if (authentication == null || !authentication.isAuthenticated()) {
         		throw new AccessDeniedException("로그인 정보가 없습니다.");
         	}
         		
         	MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
         	Long currentMemberNo = memberDetails.getMember().getMemberNo();
         	
         	if (!memberNo.equals(currentMemberNo)) {
         		throw new AccessDeniedException("접근 권한이 없습니다.");
         	}
        	
            boardDto.setBoard_no(boardNo);
            boardDto.setDelete_files(deleteFiles);
            boardDto.setMember_no(loginMember.getMemberNo()); // ✅ 수정 시에도 로그인 사용자 주입
            if (boardDto.getIs_fixed() == null) boardDto.setIs_fixed(false);

            boardService.updateBoard(boardDto, files);

            result.put("res_code", "200");
            result.put("res_msg", "수정 성공");
            result.put("boardNo", String.valueOf(boardNo));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("res_code", "500");
            result.put("res_msg", "에러: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    @CheckPermission("BOARD_CRU")
    @DeleteMapping("/board/delete/{boardNo}")
    @ResponseBody
    public Map<String, String> deleteBoard(@PathVariable("boardNo") Long boardNo) {
        Map<String, String> resultMap = new HashMap<>();
        try {
        	Optional<Board> optionalBoard = boardService.selectBoardOne(boardNo);
            Long memberNo = optionalBoard.get().getMember().getMemberNo();
        	
        	// 본인이 아닌데 URL 을 바꿔서 진입하려고 하면 Security에 의해 차단해야 함
         	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         	
         	if (authentication == null || !authentication.isAuthenticated()) {
         		throw new AccessDeniedException("로그인 정보가 없습니다.");
         	}
         		
         	MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
         	Long currentMemberNo = memberDetails.getMember().getMemberNo();
         	
         	if (!memberNo.equals(currentMemberNo)) {
         		throw new AccessDeniedException("접근 권한이 없습니다.");
         	}
        	
            boardService.deleteBoard(boardNo);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "게시글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "게시글 삭제 중 오류: " + e.getMessage());
        }
        return resultMap;
    }
}

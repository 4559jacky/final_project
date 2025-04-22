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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.dto.PageDto;
import com.mjc.groupware.board.dto.SearchDto;
import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.entity.BoardAttach;
import com.mjc.groupware.board.service.BoardAttachService;
import com.mjc.groupware.board.service.BoardService;
import com.mjc.groupware.reply.dto.ReplyDto;
import com.mjc.groupware.reply.service.ReplyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private Logger logger = LoggerFactory.getLogger(BoardController.class);
    private final BoardService boardService;
    private final BoardAttachService boardAttachService;
    private final ReplyService replyService;

    @GetMapping("/board/create")
    public String createBoardView(Model model) {
        model.addAttribute("boardDto", new BoardDto());
        return "board/create";
    }

    @PostMapping("/board")
    @ResponseBody
    public Map<String, String> createBoard(BoardDto dto,
        @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        logger.debug("게시글 작성 요청 - is_fixed: " + dto.getIs_fixed());

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "게시글 등록 중 오류가 발생하였습니다.");

        // 제목 필수 체크
        if (dto.getBoard_title() == null || dto.getBoard_title().trim().isEmpty()) {
            resultMap.put("res_msg", "제목은 필수 입력 항목입니다.");
            return resultMap;
        }

        // 내용 필수 체크
        if (dto.getBoard_content() == null || dto.getBoard_content().trim().isEmpty() 
            || dto.getBoard_content().trim().equals("<p><br></p>")) {
            resultMap.put("res_msg", "내용은 필수 입력 항목입니다.");
            return resultMap;
        }

        // is_fixed가 null이면 false로 기본값 처리
        if (dto.getIs_fixed() == null) {
            dto.setIs_fixed(false);
        }

        // 게시글 생성 (고정글/일반글 구분 없이 하나의 서비스 메소드 사용)
        Board board = boardService.createBoard(dto, files);

        if (board == null) {
            return resultMap;
        }

        resultMap.put("res_code", "200");
        resultMap.put("res_msg", "게시글이 등록되었습니다.");
        return resultMap;
    }

    @GetMapping("/board/list")
    public String selectBoardAll(Model model, SearchDto searchDto, PageDto pageDto) {
        if (pageDto.getNowPage() == 0) pageDto.setNowPage(1);
        if (pageDto.getNumPerPage() == 0) pageDto.setNumPerPage(10);

        List<Board> fixedList = boardService.selectFixedBoardList();
        Page<Board> boardList = boardService.selectBoardAll(searchDto, pageDto);
        pageDto.setTotalPage(boardList.getTotalPages());

        model.addAttribute("fixedList", fixedList); // 고정글
        model.addAttribute("boardList", boardList.getContent()); // 일반글
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("pageDto", pageDto);

        return "board/list";
    }

    @GetMapping("/board/detail/{boardNo}")
    public String selectBoardOne(@PathVariable("boardNo") Long boardNo, Model model) {
        logger.debug("boardNo received: "+boardNo);
        List<BoardAttach> attachList = boardAttachService.selectAttachList(boardNo);
        model.addAttribute("attachList", attachList);

        Optional<Board> optionalBoard = boardService.selectBoardOne(boardNo);
        if (optionalBoard.isPresent()) {
            boardService.updateViews(boardNo);
            model.addAttribute("board", optionalBoard.get());
            List<ReplyDto> replyList = replyService.getHierarchicalRepliesByBoardNo(boardNo);
            model.addAttribute("replyList", replyList);
            return "board/detail";
        } else {
            model.addAttribute("error", "해당 게시글을 찾을 수 없습니다.");
            return "error";
        }
    }

    @GetMapping("/board/{id}/update")
    public String updateBoardView(@PathVariable("id") Long id, Model model) {
        Optional<Board> optionalBoard = boardService.selectBoardOne(id);
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

    @PostMapping("/board/{boardNo}/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateBoard(
            @PathVariable("boardNo") Long boardNo,
            @ModelAttribute BoardDto boardDto,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "deleteFiles", required = false) List<Long> deleteFiles) {
        try {
            boardDto.setBoard_no(boardNo);
            boardDto.setDelete_files(deleteFiles);
            boardService.updateBoard(boardDto, files);

            Map<String, String> result = new HashMap<>();
            result.put("res_code", "200");
            result.put("res_msg", "수정 성공");
            result.put("boardNo", String.valueOf(boardNo));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> result = new HashMap<>();
            result.put("res_code", "500");
            result.put("res_msg", "에러: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/board/delete/{boardNo}")
    @ResponseBody
    public Map<String, String> deleteBoard(@PathVariable("boardNo") Long boardNo) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "게시글 삭제 중 알 수 없는 오류가 발생했습니다.");

        try {
            boardService.deleteBoard(boardNo);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "게시글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            logger.error("게시글 삭제 중 오류 발생", e);
        }

        return resultMap;
    }
}
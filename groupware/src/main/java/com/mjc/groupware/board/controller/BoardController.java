package com.mjc.groupware.board.controller;

import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.dto.TempBoardDto;
import com.mjc.groupware.board.service.BoardService;
import com.mjc.groupware.board.entity.Board;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private Logger logger = LoggerFactory.getLogger(BoardController.class);

    private final BoardService boardService;

    // 게시글 등록 페이지
    @GetMapping("/board/create")
    public String createBoardView() {
        return "board/create";
    }

    // 게시글 등록 처리 (API)
    @PostMapping("/board/create")
    @ResponseBody
    public Map<String, String> createBoard(BoardDto dto) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "게시글 등록 중 알 수 없는 오류가 발생했습니다.");

        try {
            Long boardNo = boardService.createBoard(dto);

            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "게시글이 성공적으로 등록되었습니다.");
            resultMap.put("board_no", boardNo.toString());
        } catch (Exception e) {
            logger.error("게시글 등록 중 오류 발생", e);
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "게시글 등록 중 알 수 없는 오류가 발생했습니다.");
        }

        return resultMap;
    }

    // 게시글 수정 페이지
    @GetMapping("/board/update/{boardNo}")
    public String updateBoardView(@PathVariable("boardNo") Long boardNo, Model model) {
        BoardDto boardDto = boardService.getBoard(boardNo);
        model.addAttribute("board", boardDto);
        return "board/update";
    }

    // 게시글 수정 처리 (API)
    @PostMapping("/board/update/{boardNo}")
    @ResponseBody
    public Map<String, String> updateBoard(@PathVariable("boardNo") Long boardNo, BoardDto dto) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "게시글 수정 중 알 수 없는 오류가 발생했습니다.");

        try {
            boardService.updateBoard(boardNo, dto);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "게시글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            logger.error("게시글 수정 중 오류 발생", e);
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "게시글 수정 중 알 수 없는 오류가 발생했습니다.");
        }

        return resultMap;
    }

    // 게시글 삭제 처리 (Soft Delete)
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
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "게시글 삭제 중 알 수 없는 오류가 발생했습니다.");
        }

        return resultMap;
    }

    // 게시글 목록 조회 (API)
    @GetMapping("/board/list")
    @ResponseBody
    public Map<String, Object> getAllBoards() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "게시글 조회 중 알 수 없는 오류가 발생했습니다.");

        try {
            resultMap.put("boards", boardService.getAllBoards());
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "게시글 목록 조회가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            logger.error("게시글 목록 조회 중 오류 발생", e);
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "게시글 목록 조회 중 알 수 없는 오류가 발생했습니다.");
        }

        return resultMap;
    }

    // 임시 저장 게시글 조회 (API)
    @GetMapping("/board/temp")
    @ResponseBody
    public Map<String, Object> getTempBoardsByMember(@RequestParam("memberNo") Long memberNo) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "임시 저장된 게시글 조회 중 알 수 없는 오류가 발생했습니다.");

        try {
            resultMap.put("tempBoards", boardService.getTempBoardsByMember(memberNo));
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "임시 저장된 게시글 조회가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            logger.error("임시 저장된 게시글 조회 중 오류 발생", e);
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "임시 저장된 게시글 조회 중 알 수 없는 오류가 발생했습니다.");
        }

        return resultMap;
    }

    // 임시 저장 게시글 삭제 처리 (API)
    @PostMapping("/board/temp/delete/{boardNo}")
    @ResponseBody
    public Map<String, String> deleteTempBoard(@PathVariable("boardNo") Long boardNo) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "임시 게시글 삭제 중 오류가 발생했습니다.");

        try {
            boardService.deleteTempBoard(boardNo);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "임시 게시글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            logger.error("임시 게시글 삭제 중 오류 발생", e);
        }

        return resultMap;
    }
}
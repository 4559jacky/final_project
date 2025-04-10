package com.mjc.groupware.board.controller;

import com.mjc.groupware.board.dto.BoardAttachDto;
import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.dto.PageDto;
import com.mjc.groupware.board.dto.SearchDto;
import com.mjc.groupware.board.service.BoardAttachService;
import com.mjc.groupware.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardAttachService boardAttachService;
    private final Logger logger = LoggerFactory.getLogger(BoardController.class);

    // 게시글 목록 페이지 요청 처리 (GET)
    // 검색 조건이 없으면 기본값을 "title"로 설정하고,
    // 게시글 목록과 페이지 정보를 가져와서 board/list.html로 전달
    @GetMapping("/board/list")
    public String selectBoardList(Model model, SearchDto searchDto) {
        if (searchDto.getSearchType() == null) {
            searchDto.setSearchType("title");
        }

        List<BoardDto> boardList = boardService.selectBoardList(searchDto);
        PageDto pageDto = boardService.selectBoardPage(searchDto);

        model.addAttribute("boardList", boardList);
        model.addAttribute("page_dto", pageDto);
        model.addAttribute("searchDto", searchDto);
        return "board/list";
    }

    // 게시글 상세 보기 요청 처리 (GET)
    // 게시글 ID를 기반으로 상세정보 및 첨부파일 리스트를 조회한 뒤
    // board/detail.html로 전달
    @GetMapping("/board/{id}")
    public String selectBoardOne(@PathVariable("id") Long id, Model model) {
        BoardDto board = boardService.selectBoardDetail(id);
        List<BoardAttachDto> attachList = boardService.selectAttachList(id);

        model.addAttribute("board", board);
        model.addAttribute("attachList", attachList);
        return "board/detail";
    }

    // 게시글 작성 페이지 뷰 요청 (GET)
    // board/create.html로 이동
    @GetMapping("/board/create")
    public String createBoardView() {
        return "board/create";
    }
    
    @PostMapping("/board/create")
    public String createBoard(@ModelAttribute BoardDto boardDto,
                              @RequestParam(value = "files", required = false) MultipartFile[] files) {
        boardService.createBoard(boardDto, files); // 파일까지 처리
        return "redirect:/board/list"; // 작성 후 목록 페이지로 이동
    }

    // 게시글 등록 처리 (POST)
    // form 데이터(BoardDto)와 파일 업로드(MultipartFile[])를 받아
    // 게시글 생성 후 결과 메시지를 JSON으로 반환
//    @PostMapping("/board/create")
//    @ResponseBody
//    public Map<String, String> createBoardApi(@ModelAttribute BoardDto dto,
//                                              @RequestParam(value = "files", required = false) MultipartFile[] files) {
//        Map<String, String> resultMap = new HashMap<>();
//        try {
//            boardService.createBoard(dto, files);
//            resultMap.put("res_code", "200");
//            resultMap.put("res_msg", "게시글이 등록되었습니다.");
//        } catch (Exception e) {
//            logger.error("게시글 등록 중 오류 발생", e);
//            resultMap.put("res_code", "500");
//            resultMap.put("res_msg", "게시글 등록 중 오류 발생");
//        }
//        return resultMap;
//    }

    // 게시글 수정 페이지 뷰 요청 (GET)
    // 게시글 ID를 기반으로 수정 대상 데이터를 불러오고
    // board/update.html로 전달
    @GetMapping("/board/{id}/update")
    public String updateBoardView(@PathVariable("id") Long id, Model model) {
        BoardDto board = boardService.selectBoardDetail(id);
        List<BoardAttachDto> attachList = boardService.selectAttachList(id);

        model.addAttribute("board", board);
        model.addAttribute("attachList", attachList);
        return "board/update";
    }

    // 게시글 수정 처리 (POST)
    // 게시글 번호와 form 데이터(BoardDto), 첨부파일을 받아
    // 수정 처리 후 결과 메시지를 JSON으로 반환
    @PostMapping("/board/{id}/update")
    @ResponseBody
    public Map<String, String> updateBoardApi(@PathVariable("id") Long id,
                                              @ModelAttribute BoardDto dto,
                                              @RequestParam(value = "files", required = false) MultipartFile[] files) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            dto.setBoard_no(id); // DTO에 ID를 명시적으로 설정
            boardService.updateBoard(dto, files);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "게시글이 수정되었습니다.");
        } catch (Exception e) {
            logger.error("게시글 수정 중 오류 발생", e);
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "게시글 수정 중 오류 발생");
        }
        return resultMap;
    }

    // 게시글 삭제 처리 (DELETE)
    // 게시글 ID를 받아 삭제 후 결과 메시지를 JSON으로 반환
    @DeleteMapping("/board/{id}")
    @ResponseBody
    public Map<String, String> deleteBoardApi(@PathVariable("id") Long id) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            boardService.deleteBoard(id);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "게시글이 삭제되었습니다.");
        } catch (Exception e) {
            logger.error("게시글 삭제 중 오류 발생", e);
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "게시글 삭제 중 오류 발생");
        }
        return resultMap;
    }
    // 삭제되지 않은 것처럼 남기고, 실제 목록에서 안보이게 하고 싶을때 쓰는 코드
//    @DeleteMapping("/board/{id}")
//    @ResponseBody
//    public Map<String, String> deleteBoard(@PathVariable("id") Long boardNo) {
//        Map<String, String> result = new HashMap<>();
//        try {
//            boardService.deleteBoardSoft(boardNo);
//            result.put("res_code", "200");
//            result.put("res_msg", "삭제되었습니다.");
//        } catch (Exception e) {
//            result.put("res_code", "500");
//            result.put("res_msg", "삭제 중 오류 발생");
//        }
//        return result;
//    }

    // 특정 게시글에 대한 첨부파일 목록 조회 (AJAX 요청 처리용)
    @GetMapping("/board/{board_no}/attach")
    @ResponseBody
    public List<BoardAttachDto> getAttachList(@PathVariable("board_no") Long boardNo) {
        return boardAttachService.selectAttachList(boardNo);
    }
}
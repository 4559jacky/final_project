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


import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private Logger logger = LoggerFactory.getLogger(BoardController.class);
    // 생성자 주입
    private final BoardService boardService;
    private final BoardAttachService boardAttachService;

    // 게시글 등록 페이지
    @GetMapping("/board/create")
    public String createBoardView() {
        return "board/create";
    }

    // 게시글 등록 처리 (API)
    @PostMapping("/board")
    @ResponseBody
    public Map<String, String> createBoard(BoardDto dto, @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "게시글 등록 중 오류가 발생하였습니다.");

        Board board = boardService.createBoard(dto, files); // 파일 리스트 함께 전달

        if (board == null) {
            return resultMap;
        }

        resultMap.put("res_code", "200");
        resultMap.put("res_msg", "게시글이 등록되었습니다.");
        return resultMap;
    }

    
    
    
    // 게시글 목록 조회 (API)
    @GetMapping("/board/list")
    public String selectBoardAll(Model model, SearchDto searchDto, PageDto pageDto) {
       
    	if (pageDto.getNowPage() == 0) pageDto.setNowPage(1);
    	if (pageDto.getNumPerPage() == 0) pageDto.setNumPerPage(10); // 기본값 설정
        
        // 페이징, 검색을 고려한 게시글 리스트 조회
        Page<Board> boardList = boardService.selectBoardAll(searchDto, pageDto);
        
        // 페이지 관련 정보 설정
        pageDto.setTotalPage(boardList.getTotalPages());
        
        // 모델에 데이터를 추가
        model.addAttribute("boardList", boardList.getContent()); // .getContent()로 실제 게시글 리스트를 가져옴
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("pageDto", pageDto);
        
        // 게시글 목록을 위한 list.html로 이동
        return "board/list";
    }
    
    
    // 게시글 상세보기
    @GetMapping("/board/detail/{boardNo}")
    public String selectBoardOne(@PathVariable("boardNo") Long boardNo, Model model) {
    	
    	// 코드 추가
    	List<BoardAttach> attachList = boardAttachService.selectAttachList(boardNo);
    	model.addAttribute("attachList", attachList);
    	
    	
        Optional<Board> optionalBoard = boardService.selectBoardOne(boardNo);

        if (optionalBoard.isPresent()) {
            // 조회수 증가
            boardService.viewCount(boardNo); // 조회수 증가 메소드 호출

            model.addAttribute("board", optionalBoard.get());
            return "board/detail";
        } else {
            model.addAttribute("error", "해당 게시글을 찾을 수 없습니다.");
            return "error";  // 에러 페이지로 이동
        }
    }
    
    
    
    // 게시글 수정 페이지 (수정 화면 띄우기)
    @GetMapping("/board/{id}/update")
    public String updateBoardView(@PathVariable("id") Long id, Model model) {
        Optional<Board> optionalBoard = boardService.selectBoardOne(id);

        if (optionalBoard.isPresent()) {
            model.addAttribute("board", optionalBoard.get());  // 게시글 객체 전달

            // 첨부파일 목록 조회
            List<BoardAttach> attachList = boardAttachService.selectAttachList(id);
            model.addAttribute("attachList", attachList);

            return "board/update";  // 수정 페이지로 이동
        } else {
            model.addAttribute("error", "해당 게시글을 찾을 수 없습니다.");
            return "error";  // 에러 페이지로 이동
        }
    }
    

    // 게시글 수정 처리 (API)
    @PostMapping("/board/{boardNo}/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateBoard(
            @PathVariable("boardNo") Long boardNo,
            @ModelAttribute BoardDto boardDto,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "deleteFiles", required = false) List<Long> deleteFiles) {  // 삭제할 파일 ID 리스트 추가

        try {
            boardDto.setBoard_no(boardNo);

            // 삭제할 파일 리스트를 BoardDto에 세팅
            boardDto.setDeleteFiles(deleteFiles);

            boardService.updateBoard(boardDto, files);  // 파일 리스트와 삭제 파일 리스트 함께 전달

            Map<String, String> result = new HashMap<>();
            result.put("res_code", "200");
            result.put("res_msg", "수정 성공");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> result = new HashMap<>();
            result.put("res_code", "500");
            result.put("res_msg", "에러: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    

    // 게시글 삭제 처리
    @PostMapping("/board/delete/{boardNo}")
    @ResponseBody
    public Map<String, String> deleteBoard(@PathVariable("boardNo") Long boardNo) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "게시글 삭제 중 알 수 없는 오류가 발생했습니다.");

        try {
            boardService.deleteBoard(boardNo); // 서비스 호출
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "게시글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            logger.error("게시글 삭제 중 오류 발생", e);
        }

        return resultMap;
    }
    
}
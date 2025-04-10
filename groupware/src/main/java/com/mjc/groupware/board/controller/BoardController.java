package com.mjc.groupware.board.controller;


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.dto.PageDto;
import com.mjc.groupware.board.dto.SearchDto;
import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController { 
	 
	 private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

	    private final BoardService service;

	    @GetMapping("/board/create")
	    public String createBoardView() {
	        return "board/create";
	    }

//	    @PostMapping("/board/create")
//	    @ResponseBody
//	    public Map<String,String> createBoardApi(BoardDto dto){
//	    	service.createBoard(dto);
//	    	for(MultipartFile mf : dto.getFiles()) {
//	    		System.out.println("ddddd");
//	    	}
//	    }
//	    public String createBoard(@ModelAttribute BoardDto board_dto) {
//	        service.createBoard(board_dto);
//	        return "redirect:/board/list";
	    

	    @GetMapping("/board/list")
	    public String selectBoardAll(Model model,
	                                  @ModelAttribute SearchDto searchDto,
	                                  @ModelAttribute PageDto pageDto) {
	    	if(searchDto == null) {
	    		searchDto = new SearchDto(); // 기본 생성
	    	}

	        try {
	            Page<Board> boardList = service.selectBoardAll(searchDto, pageDto);
	            pageDto.setTotalPage(boardList.getTotalPages());

	            model.addAttribute("boardList", boardList);
	            model.addAttribute("searchDto", searchDto);
	            model.addAttribute("pageDto", pageDto);
	            
	        } catch (Exception e) {
	            model.addAttribute("errorMessage", "게시글 목록을 조회하는 데 오류가 발생했습니다.");
	        }

	        return "board/list";
	    }

	    @GetMapping("/board/{id}")
	    public String selectBoardOne(@PathVariable("id") Long id, Model model) {
	        logger.info("게시글 단일 조회 : " + id);
	        Board result = service.selectBoardOne(id);
	        model.addAttribute("board", result);
	        return "board/detail";
	    }

	    @GetMapping("/board/{id}/update")
	    public String updateBoardView(@PathVariable("id") Long id, Model model) {
	        Board entity = service.selectBoardOne(id);
	        model.addAttribute("board", entity);
	        return "board/update";
	    }

	    @PostMapping("/board/{id}/update")
	    public String updateBoard(@PathVariable Long id, @ModelAttribute BoardDto dto) {
	        if (!id.equals(dto.getBoard_no())) {
	            return "redirect:/board/" + id + "/update?error=id_mismatch";
	        }
	        Board updated = service.updateBoard(dto);
	        return (updated != null) ? "redirect:/board/" + id : "redirect:/board/" + id + "/update?error=true";
	    }

	    @DeleteMapping("/board/{id}")
	    @ResponseBody
	    public Map<String, String> deleteBoardApi(@PathVariable("id") Long id) {
	        Map<String, String> result_map = new HashMap<>();
	        result_map.put("res_code", "500");
	        result_map.put("res_msg", "게시글 삭제중 오류가 발생했습니다.");

	        int result = service.deleteBoard(id);
	        if (result > 0) {
	            result_map.put("res_code", "200");
	            result_map.put("res_msg", "게시글이 삭제되었습니다.");
	        }
	        return result_map;
	    }
	}

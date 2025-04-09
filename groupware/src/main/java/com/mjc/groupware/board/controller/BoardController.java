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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.dto.PageDto;
import com.mjc.groupware.board.dto.SearchDto;
import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController { 
	 
	private Logger logger = LoggerFactory.getLogger(BoardController.class);
	
	
	// 3. 생성자 주입 + final
	private final BoardService service;
	
	@GetMapping("/board/create")
	public String createBoardView() {
		return "board/create";
	}
	
	@PostMapping("/board/create")
	@ResponseBody
	public Map<String,String> createBoardApi(BoardDto dto) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "게시글 등록중 오류가 발생하였습니다.");
		
//		List<AttachDto> attachDtoList = new ArrayList<AttachDto>();
		
//		for(MultipartFile mf : dto.getFiles()) {
//			AttachDto attachDto = attachService.uploadFile(mf);
//			if(attachDto != null) attachDtoList.add(attachDto);
//		}
//		int result = service.createBoard(dto,attachDtoList);
//			if(result > 0) {
//				resultMap.put("res_code", "200");
//				resultMap.put("res_msg", "게시글이 등록되었습니다.");
//			}
		return resultMap;
	}
	
	
	@GetMapping("/board/list")
	public String selectBoardAll(Model model, SearchDto searchDto,
			PageDto pageDto) {
		
		if(pageDto.getNowPage() == 0) pageDto.setNowPage(1);
		
		// 1. DB에서 목록 SELECT
		Page<Board> resultList = service.selectBoardAll(searchDto,pageDto);
		
		pageDto.setTotalPage(resultList.getTotalPages());
		
		// 2. 목록 Model에 등록
		model.addAttribute("boardList", resultList);
		model.addAttribute("searchDto", searchDto);
		model.addAttribute("pageDto",pageDto);
		// 3. list.html에 데이터 셋팅
		return "board/list";
	}
	
	
	@GetMapping("/board/{id}")
	public String selectBoardOne(@PathVariable("id") Long id,Model model) {
		logger.info("게시글 단일 조회 : "+id);
		Board result = service.selectBoardOne(id);
		model.addAttribute("board", result);
		
//		List<Attach> attachList = attachService.selectAttachList(id);
//		model.addAttribute("attachList", attachList);
		return "board/detail";
	}
	
	
	@GetMapping("/board/{id}/update")
	public String updateBoardView(
		@PathVariable("id") Long id, Model model) {
		Board entity = service.selectBoardOne(id);
		model.addAttribute("board",entity);
		return "board/update";
	}
	
	@PostMapping("/board/{id}/update")
	@ResponseBody
	public Map<String,String> updateBoardApi(BoardDto param) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "게시글 수정중 오류가 발생했습니다.");
	
		Board saved = service.updateBoard(param);
		
		if(saved != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "게시글이 수정되었습니다.");
			
		}
//		List<AttachDto> attachDtoList = new ArrayList<AttachDto>();
//		
//		for(MultipartFile mf : param.getFiles()) {
//			AttachDto attachDto = attachService.uploadFile(mf);
//			if(attachDto != null) attachDtoList.add(attachDto);
//		}
//		Board saved = service.updateBoard(param, attachDtoList);
//			if(saved != null) {
//				resultMap.put("res_code", "200");
//				resultMap.put("res_msg", "게시글이 수정되었습니다.");
//			}
		return resultMap;
	}
	
	
	@DeleteMapping("/board/{id}")
	@ResponseBody
	public Map<String,String> deleteBoardApi(
			@PathVariable("id") Long id){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "게시글 삭제중 오류가 발생했습니다.");
		
		int result = service.deleteBoard(id);
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "게시글이 삭제되었습니다.");
		}
		return resultMap;
	}
	
	
}

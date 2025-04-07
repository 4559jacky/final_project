package com.mjc.groupware.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BoardController {
	@GetMapping("/board/create")
	public String createBoardView() {
		return "board/create";
	}
//	@PostMapping("/board")
//	@ResponseBody
//	public Map<String,String> createBoardApi(BoardDto dto){
//		Map<String,String> resultMap = new HashMap<String,String>();
//		resultMap.put("res_code", "404");
//		resultMap.put("res_msg", "게시글 등록중 오류가 발생했습니다.");
//		
//		return resultMap;
//	}
}

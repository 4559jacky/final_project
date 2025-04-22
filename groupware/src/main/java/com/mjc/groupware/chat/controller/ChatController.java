package com.mjc.groupware.chat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.chat.dto.ChatRoomDto;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.service.ChatRoomService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService;
	
	// 채팅방 페이지 전환
	@GetMapping("/chat")
	public String meetingRoomView(HttpSession session,Model model) {
		
		List<ChatRoom> resultList = chatRoomService.selectChatRoomAll();
		model.addAttribute("chatRoomList",resultList);
		
		return "/chat/chat";
	}
	
	// 채팅방 생성
	@PostMapping("/create")
	@ResponseBody
	public Map<String,String> createChatRoom(ChatRoomDto dto) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "채팅방 추가 중 오류가 발생하였습니다.");
		
		int result = chatRoomService.createChatRoom(dto);
		
		if(result>0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "채팅방 추가가 완료되었습니다.");
		}
		
		return resultMap;
	}
	
}

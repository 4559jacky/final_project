package com.mjc.groupware.chat.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.service.ChatRoomService;
import com.mjc.groupware.member.entity.Member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService;
	
	@GetMapping("/chat")
	public String meetingRoomView(HttpSession session,Model model) {
		
		List<ChatRoom> resultList = chatRoomService.selectChatRoomAll();
		model.addAttribute("chatRoomList",resultList);
		
		return "/chat/chat";
	}
	
}

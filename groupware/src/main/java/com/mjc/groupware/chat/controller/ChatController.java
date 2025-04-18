package com.mjc.groupware.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.mjc.groupware.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService service;
	
	@GetMapping("/chat")
	public String meetingRoomView() {
		
		return "/chat/chat";
	}
	
}

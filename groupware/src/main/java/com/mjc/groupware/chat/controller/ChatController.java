package com.mjc.groupware.chat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.chat.dto.ChatMsgDto;
import com.mjc.groupware.chat.dto.ChatRoomDto;
import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.service.ChatMsgService;
import com.mjc.groupware.chat.service.ChatRoomService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService;
	private final ChatMsgService chatMsgService;
	private final SimpMessagingTemplate messagingTemplate;
	
	// 채팅방 페이지 전환
	@GetMapping("/chat")
	public String meetingRoomView(HttpSession session,Model model) {
		
		List<ChatRoomDto> resultList = chatRoomService.selectChatRoomAll();
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
	
	// 채팅방 정보 조회
	@PostMapping("/selectChatRoom/{id}")
	@ResponseBody
	public ChatRoomDto selectChatRoomOne(@PathVariable("id") Long chatRoomNo) {
		ChatRoom chatRoom = chatRoomService.selectChatRoomOne(chatRoomNo);
	    ChatRoomDto dto = ChatRoomDto.toDto(chatRoom);
		
		return dto;
	}
	
	// 채팅 메세지 조회
	@PostMapping("/selectChatMsg/{id}")
	@ResponseBody
	public List<ChatMsgDto> selectChatMsgList(@PathVariable("id") Long chatRoomNo) {
		
		List<ChatMsg> resultlist = chatMsgService.selectChatMsgList(chatRoomNo);
		
		List<ChatMsgDto> chatMsgDtoList = new ArrayList<ChatMsgDto>();
		
		for (ChatMsg chatMsg : resultlist) {
			ChatMsgDto dto = new ChatMsgDto().toDto(chatMsg);
			chatMsgDtoList.add(dto);
		}
		
		return chatMsgDtoList;
	}
	
	// 실시간 채팅
	@MessageMapping("/chat/msg")
	public void message(ChatMsgDto dto) {
		
		 chatMsgService.createChatMsg(dto);
	  
	    // 채팅방에 메시지를 한 번만 전송 (모든 사용자에게 전송)
	    messagingTemplate.convertAndSend("/topic/chat/room/" + dto.getChat_room_no(), dto);
	}

	

	
	
	
}

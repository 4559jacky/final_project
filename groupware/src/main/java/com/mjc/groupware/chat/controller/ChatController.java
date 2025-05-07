package com.mjc.groupware.chat.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.chat.dto.ChatMappingDto;
import com.mjc.groupware.chat.dto.ChatMsgDto;
import com.mjc.groupware.chat.dto.ChatRoomDto;
import com.mjc.groupware.chat.dto.ChatRoomReadDto;
import com.mjc.groupware.chat.entity.ChatMapping;
import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.service.ChatAlarmService;
import com.mjc.groupware.chat.service.ChatMsgService;
import com.mjc.groupware.chat.service.ChatRoomService;
import com.mjc.groupware.chat.session.ChatSessionTracker;
import com.mjc.groupware.chat.session.SessionRegistry;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService;
	private final ChatMsgService chatMsgService;
	private final ChatAlarmService chatAlarmService;
	private final SimpMessagingTemplate messagingTemplate;
    private final SessionRegistry sessionRegistry; 
    private final ChatSessionTracker chatSessionTracker; 
    
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
	public Map<String,String> createChatRoom( ChatRoomDto dto) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "채팅방 추가 중 오류가 발생하였습니다.");
		
		 ChatRoom createdRoom = chatRoomService.createChatRoom(dto); // 수정됨
		
		  if (createdRoom != null) {
		        resultMap.put("res_code", "200");
		        resultMap.put("res_msg", "채팅방 추가가 완료되었습니다.");
		       
		        // 실시간 전송!!
		        System.out.println("test1"+createdRoom.getChatRoomNo());
		        ChatRoom chatRoom = chatRoomService.selectChatRoomOne(createdRoom.getChatRoomNo());
			    ChatRoomDto roomDto = ChatRoomDto.toDto(chatRoom);
			    System.out.println("test2"+roomDto);
		       
		        messagingTemplate.convertAndSend("/topic/chat/room/new", roomDto);
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
	public void message(ChatMsgDto dto, SimpMessageHeaderAccessor accessor) {
	    ChatMsg saved = chatMsgService.createChatMsg(dto);

	    // 수신자 없으면 전체 꺼내기
	    if (dto.getMember_no_list() == null || dto.getMember_no_list().isEmpty()) {
	        ChatRoom chatRoom = chatRoomService.selectChatRoomOne(dto.getChat_room_no());
	        List<Long> receiverNos = new ArrayList<>();
	        for (ChatMapping mapping : chatRoom.getMappings()) {
	            if (mapping.getMemberNo() != null) {
	                receiverNos.add(mapping.getMemberNo().getMemberNo());
	            }
	        }
	        dto.setMember_no_list(receiverNos);
	    }

	    // 메시지 전송 (모든 사람 수신)
	    messagingTemplate.convertAndSend("/topic/chat/room/" + dto.getChat_room_no(), dto);

	    // 목록 업데이트
	    dto.setSend_date(LocalDateTime.now());
	    messagingTemplate.convertAndSend("/topic/chat/room/update", dto);

	    // ✅ 안 들어와 있는 사람만 뱃지/알림 전송
	    for (Long receiverNo : dto.getMember_no_list()) {
	        if (receiverNo.equals(dto.getMember_no())) continue;

	        String sessionId = sessionRegistry.getSessionIdForMember(receiverNo);

	        boolean isInRoom = sessionId != null && chatSessionTracker.isSessionInRoom(dto.getChat_room_no(), sessionId);

	        if (!isInRoom) {
	            messagingTemplate.convertAndSend("/topic/chat/unread", dto);
	        } else {
	        }
	    }

	}

	
	// 읽음 시간 조회
	@PostMapping("/chat/unread/count")
	@ResponseBody
	public int getUnreadCount(@RequestBody ChatRoomReadDto dto) {
	    return chatRoomService.selectUnreadMsgCount(dto);
	}
	
	
	// 읽음 시간 기록
	@PostMapping("/chat/read/update")
	@ResponseBody
	public String updateReadTime(@RequestBody ChatRoomReadDto dto) {
	    chatRoomService.updateReadTime(dto);
	    messagingTemplate.convertAndSendToUser(
	    	    String.valueOf(dto.getMember_no()), // 로그인한 유저의 식별자 (Principal name도 가능)
	    	    "/queue/chat/read",
	    	    dto
	    	);


	    return "updated";
	}
	
	@MessageMapping("/chat/session-register")
	public void registerSession(Principal principal, @Payload Map<String, Object> payload, SimpMessageHeaderAccessor accessor) {
		// SimpMessageHeaderAccessor accessor = 이 메세지를 보낸 사람의 세션Id을 가져오는 객체
		
		//이 사용자가 웹소켓으로 접속한 고유한 세션ID를 가져옴
	    String sessionId = accessor.getSessionId();
	    // 클라이언트에서 보낸 사용자 번호 꺼냄
	    Long memberNo = Long.parseLong(payload.get("memberNo").toString());
	    // 이 멤버가 이 세션으로 접속했다는 걸 저장
	    sessionRegistry.register(memberNo, sessionId);

	    // roomNo도 포함된 경우만 지금 이 세션이 채팅방 roomNo 안에 들어가 있다고 서버에 등록함
	    if (payload.containsKey("roomNo")) {
	        Long roomNo = Long.parseLong(payload.get("roomNo").toString());
	        chatSessionTracker.enterRoom(roomNo, sessionId);
	    }

	}



	// 채팅방 나가기
	@PostMapping("/updateStatus")
	@ResponseBody
	public Map<String, String> updateStatus(@RequestBody ChatMappingDto dto) {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "채팅방 나가기 중 오류가 발생하였습니다.");

		int result = chatRoomService.updateStatus(dto);

		if (result > 0) {
	
			chatMsgService.sendOutSystemMsg(dto.getChat_room_no(), dto.getMember_no());
			
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "채팅방 나가기가 완료되었습니다.");
		}

		return resultMap;
	}
	
	
	
}

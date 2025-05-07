package com.mjc.groupware.chat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.chat.dto.ChatMsgDto;
import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.repository.ChatMsgRepository;
import com.mjc.groupware.chat.repository.ChatRoomRepository;
import com.mjc.groupware.chat.specification.ChatMsgSpecification;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgService {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	private final ChatMsgRepository chatMsgRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;

	
	// 채팅 메세지 조회 selectChatMsg
	public List<ChatMsg> selectChatMsgList(Long chatRoomNo) {
		
		Specification<ChatMsg> spec = (root, query, criteriaBuilder) -> null;
		spec = spec.and(ChatMsgSpecification.chatRoomNoEquals(chatRoomNo));
		
		List<ChatMsg> resultList = chatMsgRepository.findAll(spec);
		
		return resultList;
	}
	
	// 채팅 메세지 db 등록
	@Transactional(rollbackFor = Exception.class)
	public ChatMsg createChatMsg(ChatMsgDto dto) {
		ChatMsg saved = null; 
	    try {
	        ChatMsg chatMsg = ChatMsg.builder()
	                .chatMsgContent(dto.getChat_msg_content())
	                .chatRoomNo(ChatRoom.builder().chatRoomNo(dto.getChat_room_no()).build())
	                .memberNo(Member.builder().memberNo(dto.getMember_no()).build())
	                .build();

	         saved = chatMsgRepository.save(chatMsg);

	        // ✅ 기존 ChatRoom 조회
	        ChatRoom chatRoom = chatRoomRepository.findById(dto.getChat_room_no())
	                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않음"));

	        chatRoom.setLastMsg(saved.getChatMsgContent());
	        chatRoom.setLastMsgDate(saved.getSendDate());

	        chatRoomRepository.save(chatRoom);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return saved;
	}
	
	// 나가기 시스템 메세지 저장
	public void sendOutSystemMsg(Long chatRoomNo, Long memberNo) {
		Member member = memberRepository.findById(memberNo).orElseThrow();
		ChatRoom room = chatRoomRepository.findById(chatRoomNo).orElseThrow();
		
		ChatMsg chatMsg = new ChatMsg();
		chatMsg.setChatRoomNo(room);
		chatMsg.setMemberNo(member);
		chatMsg.setChatMsgContent(member.getMemberName() + "님이 채팅방을 나갔습니다.");
		chatMsg.setChatMsgType("Y");
		
		chatMsgRepository.save(chatMsg);
				          
		 // 웹소켓으로 시스템 메시지 브로드캐스트
	    ChatMsgDto dto = new ChatMsgDto().toDto(chatMsg); // DTO로 변환
	    messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoomNo, dto);				
	}

}

package com.mjc.groupware.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.chat.dto.ChatMappingDto;
import com.mjc.groupware.chat.dto.ChatRoomDto;
import com.mjc.groupware.chat.entity.ChatMapping;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.repository.ChatMappingrepository;
import com.mjc.groupware.chat.repository.ChatRoomRepository;
import com.mjc.groupware.chat.specification.ChatMappingSpecification;
import com.mjc.groupware.chat.specification.ChatRoomSpecification;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.security.MemberDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
	
	private final ChatRoomRepository chatRoomRepository;
	private final ChatMappingrepository mappingRepository;
	
	
	// 채팅 화면 전환
	public List<ChatRoomDto> selectChatRoomAll(){
		
		// 현재 로그인한 사용자의 인증정보를 SecurityContextHolder에서 꺼내옴
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 인증된 사용자의 상세 정보를 담아줌
		MemberDetails md = (MemberDetails)authentication.getPrincipal();

		// 참여 중인 채팅방만 필터링 (memberStatus = "Y")
		Specification<ChatRoom> spec = ChatRoomSpecification.participatedBy(md.getMember());
		
		// 정렬 조건 - lastDate 내림차순
	    Sort sort = Sort.by(Sort.Direction.DESC, "lastMsgDate");

	    // 조회하고 조회결과 return 
	    List<ChatRoom> list = chatRoomRepository.findAll(spec, sort);
	    
	    List<ChatRoomDto> result = new ArrayList<>();
	    
	    for(ChatRoom room :list) {
	    	String title = room.getChatRoomTitle();
	    	
	    	if(title.trim().isEmpty() || title.equals("")) {
	    		List<String> nameList = new ArrayList<String>();
	    		
	    		for(ChatMapping mapping : room.getMappings()) {
	    			Member member = mapping.getMemberNo();
	    			
	    			 if (!member.getMemberNo().equals(md.getMember().memberNo)) {
	                     String pos = member.getPos() != null ? member.getPos().getPosName() : "";
	                     nameList.add(member.getMemberName() + " " + pos);
	                 }
        		}
	    		title = String.join(", ", nameList);
	    	}
	    
	    	ChatRoomDto dto = ChatRoomDto.builder()
	                .chat_room_no(room.getChatRoomNo())
	                .chat_room_title(title)
	                .last_msg(room.getLastMsg())
	                .last_msg_date(room.getLastMsgDate())
	                .build();

	        result.add(dto);
	    }
	   
	    return result;
	}
	
	// 채팅방 생성
	@Transactional(rollbackFor = Exception.class)
	public int createChatRoom(ChatRoomDto dto) {
		int result = 0;
		try {
			
			ChatRoom chatroom = ChatRoom.builder()
					.chatRoomTitle(dto.getChat_room_title())
					.createMemberNo(Member.builder().memberNo(dto.getCreate_member_no()).build())
					.build();
			
			ChatRoom saved = chatRoomRepository.save(chatroom);
			
			//  채팅 매핑 저장
			for (Long memberNo : dto.getMember_no()) {
				ChatMapping mappings= ChatMapping.builder()
						.chatRoomNo(saved)
						.memberNo(Member.builder().memberNo(memberNo).build())
						.build();
				
				mappingRepository.save(mappings);
			}
			
			result++;
			
		}catch(Exception e ) {
			e.printStackTrace();
		}

		return result;
	}

	// 채팅방 상세 조회
	public ChatRoom selectChatRoomOne(Long chatRoomNo) {
		return chatRoomRepository.findById(chatRoomNo).orElse(null);
	}
	
	// 채팅방 나가기 
	public int updateStatus(ChatMappingDto dto) {
	    int result = 0;
	    
	    try {
	        Specification<ChatMapping> spec = ChatMappingSpecification.ChatRoomNoEqualsAndMemberNoEquals(dto);
	        List<ChatMapping> list = mappingRepository.findAll(spec);

	       
	        for (ChatMapping mapping : list) {
	            mapping.setMemberStatus("N");
	        }

	        mappingRepository.saveAll(list);
	        result = list.size();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return result;
	}

}

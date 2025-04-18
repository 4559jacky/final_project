package com.mjc.groupware.chat.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.repository.ChatRoomRepository;
import com.mjc.groupware.chat.specification.ChatRoomSpecification;
import com.mjc.groupware.member.security.MemberDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
	
	private final ChatRoomRepository chatRoomRepository;
	
	public List<ChatRoom> selectChatRoomAll(){
		
		// 현재 로그인한 사용자의 인증정보를 SecurityContextHolder에서 꺼내옴
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 인증된 사용자의 상세 정보를 담아줌
		MemberDetails md = (MemberDetails)authentication.getPrincipal();
		
		// JPA 동적 쿼리 만드는 인터페이스 - 처음에는 null로 시작 
		Specification<ChatRoom> spec = (root, query, criteriaBuilder) -> null;
		
		// 채팅 시작자가 로그인 사용자랑 같은가 
		spec = spec.and(ChatRoomSpecification.fromMemberEquals(md.getMember()));
		// 내가 시작한 채팅방과 상대가 시작한 채팅방 모두 가져오기 
		spec = spec.or(ChatRoomSpecification.toMemberEquals(md.getMember()));
		
		// 정렬 조건 - lastDate 내림차순
	    Sort sort = Sort.by(Sort.Direction.DESC, "lastMessageDate");
	    
	    

	    // 조회하고 조회결과 return 
	    List<ChatRoom> list = chatRoomRepository.findAll(spec, sort);
		
		return list;
		
	}
	
}

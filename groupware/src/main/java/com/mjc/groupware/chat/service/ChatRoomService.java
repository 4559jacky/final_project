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
import com.mjc.groupware.member.entity.Member;
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

		// 참여 중인 채팅방만 필터링 (memberStatus = "Y")
		Specification<ChatRoom> spec = ChatRoomSpecification.participatedBy(md.getMember());
		
		// 정렬 조건 - lastDate 내림차순
	    Sort sort = Sort.by(Sort.Direction.DESC, "lastMsgDate");

	    // 조회하고 조회결과 return 
	    List<ChatRoom> list = chatRoomRepository.findAll(spec, sort);
		System.out.println(list);
		return list;
		
	}
	
}

package com.mjc.groupware.chat.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.chat.entity.ChatAlarm;
import com.mjc.groupware.member.entity.Member;

public interface  ChatAlarmRepository extends JpaRepository<ChatAlarm, Long>{

	List<ChatAlarm> findAll(Specification<ChatAlarm> spec, Sort by);

	List<ChatAlarm> findByChatRoomNo_ChatRoomNoAndMemberNo_MemberNoAndReadStatus(
		    Long chatRoomNo, Long memberNo, String readStatus
		);

	
}

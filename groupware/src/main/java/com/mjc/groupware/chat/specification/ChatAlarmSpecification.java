package com.mjc.groupware.chat.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.chat.entity.ChatAlarm;
import com.mjc.groupware.member.entity.Member;

public class ChatAlarmSpecification {

	 public static Specification<ChatAlarm> unreadByMember(Member member) {
	        return (root, query, cb) -> cb.and(
	                cb.equal(root.get("memberNo").get("memberNo"), member.getMemberNo()),
	                cb.equal(root.get("readStatus"), "N")
	        );
	    }
	
}

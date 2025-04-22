package com.mjc.groupware.chat.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class ChatRoomSpecification {
	
	
    // 내가 참여하는 채팅방 정보 가져옴 
    public static Specification<ChatRoom> participatedBy(Member member) {
        return (root, query, cb) -> {
        	// 중복 제거 
        	query.distinct(true);
        	// chat_room 테이블 → chat_mapping 테이블 조인
            Join<Object, Object> mappings = root.join("mappings", JoinType.LEFT);
            // 내가 참여한 방인지 확인
            Predicate isMyMapping = cb.equal(mappings.get("memberNo"), member);
            // 나의 참여 상태 확인 
            Predicate isActive = cb.equal(mappings.get("memberStatus"), "Y");
            // 위 조건을 동시에 만족해야함 
            return cb.and(isMyMapping, isActive);
        };
    }
}
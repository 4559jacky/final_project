package com.mjc.groupware.chat.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>{

	List<ChatRoom> findAll(Specification<ChatRoom> spec, Sort sort);
	
}

package com.mjc.groupware.chat.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.chat.entity.ChatMsg;

public interface ChatMsgRepository  extends JpaRepository<ChatMsg, Long>{

	
	List<ChatMsg> findAll(Specification<ChatMsg> spec);
}

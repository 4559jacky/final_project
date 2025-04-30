package com.mjc.groupware.chat.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.chat.entity.ChatMapping;
import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.entity.ChatRoom;

public interface ChatMappingrepository  extends JpaRepository<ChatMapping, Long>, JpaSpecificationExecutor<ChatMapping>{
	
	List<ChatMapping> findAll(Specification<ChatMapping> spec);
}

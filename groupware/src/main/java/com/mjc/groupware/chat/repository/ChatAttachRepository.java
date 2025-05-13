package com.mjc.groupware.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.chat.entity.ChatAttach;
import com.mjc.groupware.chat.entity.ChatMsg;

public interface ChatAttachRepository extends JpaRepository<ChatAttach, Long>{

	List<ChatAttach> findAll(Specification<ChatAttach> spec);
	

}


package com.mjc.groupware.chat.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.chat.entity.ChatRoomRead;

public interface ChatRoomReadRepository extends JpaRepository<ChatRoomRead, Long>{

	List<ChatRoomRead> findAll(Specification<ChatRoomRead> spec);

}

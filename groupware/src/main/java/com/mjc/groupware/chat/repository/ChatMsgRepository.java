package com.mjc.groupware.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.chat.entity.ChatMsg;

public interface ChatMsgRepository  extends JpaRepository<ChatMsg, Long>{

}

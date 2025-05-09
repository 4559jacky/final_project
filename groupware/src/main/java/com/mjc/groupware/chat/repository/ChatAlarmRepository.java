package com.mjc.groupware.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.chat.entity.ChatAlarm;
import com.mjc.groupware.member.entity.Member;

public interface  ChatAlarmRepository extends JpaRepository<ChatAlarm, Long>{

}

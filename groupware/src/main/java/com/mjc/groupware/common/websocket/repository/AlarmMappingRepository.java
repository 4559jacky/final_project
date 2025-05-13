package com.mjc.groupware.common.websocket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.common.websocket.entity.AlarmMapping;

public interface AlarmMappingRepository extends JpaRepository<AlarmMapping, Long> {

}

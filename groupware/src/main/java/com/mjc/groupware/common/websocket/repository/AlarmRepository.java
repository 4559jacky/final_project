package com.mjc.groupware.common.websocket.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.common.websocket.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {


}

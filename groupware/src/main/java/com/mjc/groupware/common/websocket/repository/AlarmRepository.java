package com.mjc.groupware.common.websocket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.common.websocket.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    // ✔ 이미 동일한 게시글에 대한 "투표 마감 알림"이 존재하는지 확인
    Optional<Alarm> findByBoardAndAlarmTitle(Board board, String alarmTitle);

}

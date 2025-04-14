package com.mjc.groupware.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.board.entity.TempBoard;

public interface TempBoardRepository extends JpaRepository<TempBoard, Long> {

    List<TempBoard> findByMemberNo(Long memberNo);
}
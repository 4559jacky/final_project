package com.mjc.groupware.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.mjc.groupware.board.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
}
package com.mjc.groupware.board.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.mjc.groupware.board.entity.Board;


/**
 * Board 엔터티에 대한 데이터베이스 접근을 처리하는 리포지토리 인터페이스입니다.
 */
public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    Page<Board> findAll(Specification<Board> spec, Pageable pageable);
}

package com.mjc.groupware.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    
    Optional<Board> findByBoardNo(Long boardNo);

    List<Board> findByMemberMemberNoAndBoardStatus(Long memberNo, String boardStatus);

    Page<Board> findAll(Specification<Board> spec, Pageable pageable);

    List<Board> findByBoardStatus(String boardStatus);

    @Modifying(clearAutomatically = true) // 영속성 컨텍스트 클리어 보장
    @Query(value = "UPDATE board SET views = views + 1 WHERE board_no = :boardNo", nativeQuery = true)
    void updateViews(@Param("boardNo") Long boardNo);

    List<Board> findByIsFixedTrueOrderByRegDateDesc();  // 고정글만
    Page<Board> findByIsFixedFalse(Specification<Board> spec, Pageable pageable);  // 일반글만

	List<Board> findByIsFixedTrueAndBoardStatusNot(String string, Sort by);

	Page<Board> findByBoardStatusOrderByIsFixedDescRegDateDesc(String boardStatus, Pageable pageable);
	

}
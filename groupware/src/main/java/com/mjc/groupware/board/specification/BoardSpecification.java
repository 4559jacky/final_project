package com.mjc.groupware.board.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.board.entity.Board;

/**
 * 검색 조건을 동적으로 생성하는 Specification 클래스입니다.
 */
public class BoardSpecification {
	// 제목에 특정 문자열이 포함된 검색 조건
    public static Specification<Board> boardTitleContains(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("boardTitle"), "%" + keyword + "%");
    }
    // 내용에 특정 문자열이 포함된 검색 조건
    public static Specification<Board> boardContentContains(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("boardContent"), "%" + keyword + "%");
    }
}


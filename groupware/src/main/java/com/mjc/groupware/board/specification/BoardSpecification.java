package com.mjc.groupware.board.specification;

import com.mjc.groupware.board.entity.Board;

import org.springframework.data.jpa.domain.Specification;

public class BoardSpecification {

	 // 제목에 특정 문자열이 포함된 검색 조건
    public static Specification<Board> boardTitleContains(String keyword) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("boardTitle"), "%" + keyword + "%");
    }

    // 내용에 특정 문자열이 포함된 검색 조건
    public static Specification<Board> boardContentContains(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return null;
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.like(criteriaBuilder.lower(root.get("boardContent")), "%" + keyword.toLowerCase() + "%");
    }
    // 게시글 삭제 여부 ("N" -> "Y")
    public static Specification<Board> notDeleted() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("boardStatus"), "N");
    }

}
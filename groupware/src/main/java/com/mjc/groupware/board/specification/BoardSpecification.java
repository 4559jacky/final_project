package com.mjc.groupware.board.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.board.entity.Board;

public class BoardSpecification {
	 public static Specification<Board> search(String keyword) {
	        return (root, query, cb) -> {
	            if (keyword == null || keyword.trim().isEmpty()) return null;
	            return cb.or(
	                cb.like(root.get("board_title"), "%" + keyword + "%"),
	                cb.like(root.get("board_content"), "%" + keyword + "%")
	            );
	        };
	    }
	}


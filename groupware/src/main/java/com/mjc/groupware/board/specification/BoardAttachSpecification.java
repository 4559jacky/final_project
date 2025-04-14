package com.mjc.groupware.board.specification;


import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.entity.BoardAttach;
import org.springframework.data.jpa.domain.Specification;

public class BoardAttachSpecification {

    public static Specification<BoardAttach> boardEquals(Board board) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("board"), board);
    }
}
package com.mjc.groupware.board.specification;

import com.mjc.groupware.board.entity.BoardAttach;
import org.springframework.data.jpa.domain.Specification;

public class BoardAttachSpecification {

	// 게시글 번호로 필터링하는 스펙ification 생성 메소드
	public static Specification<BoardAttach> hasBoardNo(Long boardNo) {
	    // root: 현재 쿼리의 루트 엔티티, query: 쿼리 객체, cb: CriteriaBuilder
	    return (root, query, cb) -> cb.equal(root.get("board_no"), boardNo); // board_no 필드가 주어진 boardNo와 같은지 비교
	}

	// 파일 이름에 키워드가 포함된 경우 필터링하는 스펙ification 생성 메소드
	public static Specification<BoardAttach> fileNameContains(String keyword) {
	    // root: 현재 쿼리의 루트 엔티티, query: 쿼리 객체, cb: CriteriaBuilder
	    return (root, query, cb) -> cb.like(root.get("ori_name"), "%" + keyword + "%"); // ori_name 필드가 주어진 키워드를 포함하는지 검사
	}
}
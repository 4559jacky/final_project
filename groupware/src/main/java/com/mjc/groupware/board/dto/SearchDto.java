package com.mjc.groupware.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SearchDto {
    private String keyword = ""; // 검색어
    private int nowPage = 1; // 현재 페이지
    private int numPerPage = 10; // 페이지당 게시글 수
    private String search_type = ""; // 검색 타입
    private int order_type = 1; // 정렬 타입 (기본값: 최신순)
    private String search_text = ""; // 검색 텍스트 추가
}
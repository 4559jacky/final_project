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
    private String keyword = "";
    private int nowPage = 1;
    private int numPerPage = 10;
    private int search_type = 0; // <== String으로 변경
    private int order_type = 1;
    private String search_text = ""; // 실제 검색 키워드
}
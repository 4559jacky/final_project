package com.mjc.groupware.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SearchDto {
	  // 검색 관련 코드
	 private int search_type; // 검색(제목, 내용 등)
	 private String search_text; // 검색 저장
	 private int order_type; // 정렬 (최신순, 오래된순, 조회수순)
	 
	 private int nowPage = 1;
	 private int numPerPage = 3;
	 
	 private String searchType;
	 private String searchKeyword;
	 
//	 private int numPerPage = 3; // 한 페이지에 보일 게시글 수
//	 private int nowPage;        // 현재 페이지
//	 private String searchKeyword; // 검색 키워드
//	 private String searchType;    // 검색 유형 (예: 제목, 내용 등)
	} 

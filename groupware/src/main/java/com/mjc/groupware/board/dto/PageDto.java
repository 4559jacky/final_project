package com.mjc.groupware.board.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageDto {
    private int numPerPage = 10; // 페이지당 게시글 수
    private int nowPage; // 현재 페이지
    private int pageBarSize = 5; // 페이지 바 크기
    private int pageBarStart; // 페이지 바 시작
    private int pageBarEnd; // 페이지 바 끝
    private boolean prev = true; // 이전 버튼
    private boolean next = true; // 다음 버튼
    private int totalPage; // 총 페이지 수
    private int totalCount;
    
    private List<BoardDto> boardList; // 게시글 DTO 리스트

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        calcPaging();
    }

    public void calcPaging() {
    	if (totalPage == 0) {
			pageBarStart = 1;
			pageBarEnd = 1;
			prev = false;
			next = false;
			return;
		}
        pageBarStart = ((nowPage - 1) / pageBarSize) * pageBarSize + 1;
        pageBarEnd = pageBarStart + pageBarSize - 1;
        if (pageBarEnd > totalPage) pageBarEnd = totalPage;
        if (pageBarStart == 1) prev = false;
        if (pageBarEnd >= totalPage) next = false;
    }

    public void setBoardList(List<BoardDto> boardList) {
        this.boardList = boardList; // 게시글 리스트 설정
    }
}
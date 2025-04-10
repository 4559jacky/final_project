package com.mjc.groupware.board.dto;


import java.util.List;

import com.mjc.groupware.board.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageDto {
    private int numPerPage = 3;
    private int nowPage;
    private int pageBarSize = 2;
    private int pageBarStart;
    private int pageBarEnd;
    private boolean prev = true;
    private boolean next = true;
    private int totalPage;
    
    private List<Board> boardList;  // 게시글 리스트를 추가

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        calcPaging();
    }

    public void calcPaging() {
        pageBarStart = ((nowPage - 1) / pageBarSize) * pageBarSize + 1;
        pageBarEnd = pageBarStart + pageBarSize - 1;
        if (pageBarEnd > totalPage) pageBarEnd = totalPage;
        if (pageBarStart == 1) prev = false;
        if (pageBarEnd >= totalPage) next = false;
    }

    public void setBoardList(List<Board> boardList) {
        this.boardList = boardList;  // 게시글 리스트 설정
    }
}
package com.mjc.groupware.attendance.dto;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AttendPageDto {
	
	private int numPerPage = 10;		// 한 페이지에 데이터 개수
	private int nowPage;			// 현재 페이지
	
	// 페이징바
	private int pageBarSize = 5;
	private int pageBarStart;
	private int pageBarEnd;
	
	// 이전, 다음 여부
	private boolean prev = true;
	private boolean next = true;
	
	private int totalPage;			// 총 페이지 수
	private int totalCount;
	
	public void setNowPage(int nowPage) {
		this.nowPage = nowPage;
	}
	
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
		calcPaging();
	}
	
	public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
	
	private void calcPaging() {
		if (totalPage == 0) {
			pageBarStart = 1;
			pageBarEnd = 1;
			prev = false;
			next = false;
			return;
		}
		
		pageBarStart = ((nowPage-1)/pageBarSize)*pageBarSize + 1;	// 페이징바의 시작 번호 계산
		
		pageBarEnd = pageBarStart + pageBarSize -1;					// 페이징바의 끝 번호 계산(만일 끝번호가 전체 페이지 개수보다 크면 재할당)
		
		if(pageBarEnd > totalPage) pageBarEnd = totalPage;			// 이전, 이후 버튼이 보이는지 여부 계산
		if(pageBarStart == 1) prev = false;
		if(pageBarEnd >= totalPage) next = false;
	}
	
}


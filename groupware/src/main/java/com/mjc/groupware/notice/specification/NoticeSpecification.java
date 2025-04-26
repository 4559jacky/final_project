package com.mjc.groupware.notice.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.notice.entity.Notice;

public class NoticeSpecification {
	public static Specification<Notice> noticeTitleContains(String keyword){
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(root.get("noticeTitle"), "%"+keyword+"%");
	}
	
	public static Specification<Notice> noticeContentContains(String keyword){
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(root.get("noticeContent"),"%"+keyword+"%");
	}
	
	public static Specification<Notice> isNotFixed() {
	    return (root, query, criteriaBuilder) ->
	        criteriaBuilder.equal(root.get("noticeFix"), "N");
	}
}

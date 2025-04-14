package com.mjc.groupware.notice.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.notice.entity.Notice;

public class NoticeSpecification {
	public static Specification<Notice> sharedTitleContains(String keyword){
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(root.get("sharedTitle"), "%"+keyword+"%");
	}
	
	public static Specification<Notice> sharedContentContains(String keyword){
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(root.get("sharedContent"),"%"+keyword+"%");
	}
	
}

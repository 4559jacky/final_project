package com.mjc.groupware.shared.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.shared.entity.Shared;

public class SharedSpecification {
	public static Specification<Shared> sharedTitleContains(String keyword){
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(root.get("sharedTitle"),"%"+keyword+"%");
	}
	
	// 내용에 특정 문자열이 포함된 검색 조건
	public static Specification<Shared> boardContentContains(String keyword){
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(root.get("sharedContent"),"%"+keyword+"%");
	}
}

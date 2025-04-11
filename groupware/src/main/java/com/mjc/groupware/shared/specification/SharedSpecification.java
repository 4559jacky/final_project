package com.mjc.groupware.shared.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.shared.entity.Shared;

public class SharedSpecification {
	public static Specification<Shared> sharedTitleContains(String keyword){
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(root.get("sharedTitle"), "%"+keyword+"%");
	}
	
	public static Specification<Shared> sharedContentContains(String keyword){
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(root.get("sharedContent"),"%"+keyword+"%");
	}
	
}

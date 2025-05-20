package com.mjc.groupware.company.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.company.entity.Func;

public class FuncSpecification {
	
	public static Specification<Func> parentFuncIsNull() {
		return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("parentFunc"));
	}
	
}

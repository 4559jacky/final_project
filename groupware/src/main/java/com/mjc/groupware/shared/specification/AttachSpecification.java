package com.mjc.groupware.shared.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.shared.entity.Attach;
import com.mjc.groupware.shared.entity.Shared;

public class AttachSpecification {

	public static Specification<Attach> sharedEquals(Shared shared){
		return (root, query, criteriaBuilder)->criteriaBuilder.equal(root.get("shared"),shared);
	}
}

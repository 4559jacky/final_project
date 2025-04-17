package com.mjc.groupware.notice.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.notice.entity.Attach;
import com.mjc.groupware.notice.entity.Notice;

public class AttachSpecification {
	
	public static Specification<Attach> noticeEquals(Notice notice){
		return (root, query, criteriaBuilder)->criteriaBuilder.equal(root.get("notice"),notice);
	}
}

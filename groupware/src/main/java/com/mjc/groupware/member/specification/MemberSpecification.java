package com.mjc.groupware.member.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.member.entity.Member;

public class MemberSpecification {

	public static Specification<Member> equalMemberNo(Long memberNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("memberNo"), memberNo);
    }
	
}

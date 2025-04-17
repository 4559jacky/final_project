package com.mjc.groupware.member.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.member.entity.Member;

public class MemberSpecification {

	public static Specification<Member> equalMemberNo(Long memberNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("memberNo"), memberNo);
    }
	
	public static Specification<Member> memberNameContains(String keyword){
		// memberName 에 특정 문자열이 포함된 경우만 조회되도록 조건 걸어줌
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(root.get("memberName"),"%"+keyword+"%");
 	}
	
}

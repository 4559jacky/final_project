package com.mjc.groupware.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.entity.MemberAttach;

public interface MemberAttachRepository extends JpaRepository<MemberAttach, Long> {
	
	MemberAttach findTop1ByMemberOrderByRegDateDesc(Member member);
	
}

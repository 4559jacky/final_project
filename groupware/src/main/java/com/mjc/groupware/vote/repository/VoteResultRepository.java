package com.mjc.groupware.vote.repository;

import com.mjc.groupware.vote.entity.VoteResult;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteResultRepository extends JpaRepository<VoteResult, Long> {
	int countByOption_OptionNo(Long optionNo);

	boolean existsByVote_VoteNoAndMember_MemberNo(Long voteNo, Long memberNo); // 사용자 투표 여부 체크

	List<VoteResult> findByVote_VoteNo(Long voteNo);

	@Query("SELECT DISTINCT vr.member.memberNo FROM VoteResult vr WHERE vr.vote.voteNo = :voteNo AND vr.member IS NOT NULL")
	List<Long> findParticipantMemberNos(@Param("voteNo") Long voteNo);

}
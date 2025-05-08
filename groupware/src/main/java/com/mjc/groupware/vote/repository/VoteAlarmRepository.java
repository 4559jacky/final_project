package com.mjc.groupware.vote.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.vote.entity.VoteAlarm;

public interface VoteAlarmRepository extends JpaRepository<VoteAlarm, Long> {

    List<VoteAlarm> findByMember_MemberNoAndIsReadFalse(Long memberNo);

    boolean existsByVote_VoteNoAndMember_MemberNo(Long voteNo, Long memberNo);
    
}
package com.mjc.groupware.vote.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.vote.entity.Vote;
import com.mjc.groupware.vote.entity.VoteOption;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    List<VoteOption> findByVote(Vote vote);

    List<VoteOption> findByVote_VoteNo(Long voteNo); // ✅ 연관관계 경로에 맞게 수정
    
    @Transactional
    void deleteAllByVote_VoteNo(Long voteNo);
}
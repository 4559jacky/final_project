package com.mjc.groupware.vote.repository;

import com.mjc.groupware.vote.entity.Vote;
import com.mjc.groupware.vote.entity.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    List<VoteOption> findByVote(Vote vote);

    List<VoteOption> findByVote_VoteNo(Long voteNo); // ✅ 연관관계 경로에 맞게 수정
}
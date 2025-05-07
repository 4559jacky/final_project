package com.mjc.groupware.vote.repository;

import com.mjc.groupware.vote.entity.VoteResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteResultRepository extends JpaRepository<VoteResult, Long> {
	int countByOption_OptionNo(Long optionNo);
}
package com.mjc.groupware.vote.repository;

import com.mjc.groupware.vote.entity.Vote;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

	Optional<Vote> findByBoard_BoardNo(Long boardNo);
}

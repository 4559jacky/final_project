package com.mjc.groupware.vote.repository;


import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;


import com.mjc.groupware.vote.dto.VoteDto;
import com.mjc.groupware.vote.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

	Optional<Vote> findByBoard_BoardNo(Long boardNo);

	VoteDto findDtoByVoteNo(Long voteNo);
	
}

package com.mjc.groupware.vote.entity;

import java.time.LocalDateTime;

import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "vote_result")
public class VoteResult {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_no")
    private Long resultNo; // 투표 결과

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_no", nullable = false)
    private Vote vote; // 투표 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_no", nullable = false)
    private VoteOption option; // 선택한 옵션

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;  // 익명인 경우 null(투표한 사용자)

    @Column(name = "vote_time", nullable = false)
    private LocalDateTime voteTime; // 투표 시간
}

package com.mjc.groupware.vote.entity;

import com.mjc.groupware.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vote_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteResult {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_no")
    private Long resultNo;

    @ManyToOne
    @JoinColumn(name = "vote_no", nullable = false)
    private Vote vote;

    @ManyToOne
    @JoinColumn(name = "option_no", nullable = false)
    private VoteOption option;

    @ManyToOne
    @JoinColumn(name = "member_no", nullable = false) // 필수 값으로 강제
    private Member member;

    @Column(name = "vote_time", nullable = false)
    private LocalDateTime voteTime;
}
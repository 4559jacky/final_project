package com.mjc.groupware.vote.entity;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_no")
    private Long voteNo;

    @OneToOne
    @JoinColumn(name = "board_no", nullable = false)
    private Board board;

    @Column(name = "vote_title", nullable = false, length = 255)
    private String voteTitle;

    @Column(name = "is_multiple", nullable = false, length = 1)
    private String isMultiple;

    @Column(name = "is_anonymous", nullable = false, length = 1)
    private String isAnonymous;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @CreationTimestamp
    @Column(name = "reg_date", nullable = false, updatable = false)
    private LocalDateTime regDate;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> voteOptions = new ArrayList<>();
    
	}
package com.mjc.groupware.vote.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vote_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteOption {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_no")
    private Long optionNo;

    @ManyToOne
    @JoinColumn(name = "vote_no", nullable = false)
    private Vote vote;

    @Column(name = "option_text", nullable = false)
    private String optionText;

    @Column(name = "order_no", nullable = false)
    private int orderNo;
}
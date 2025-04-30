package com.mjc.groupware.vote.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "vote_option")
public class VoteOption {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_no")
    private Long optionNo; // 선택지 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_no", nullable = false)
    private Vote vote; // 투표 번호

    @Column(name = "option_text", nullable = false)
    private String optionText; // 선택지 내용

    @Column(name = "order_no", nullable = false)
    private Integer orderNo = 0; // 표시 순서

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteResult> voteResults = new ArrayList<>();
}


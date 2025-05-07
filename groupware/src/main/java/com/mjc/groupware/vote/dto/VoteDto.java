package com.mjc.groupware.vote.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VoteDto {
    private Long vote_no;
    private Long board_no;
    private String vote_title;
    private String is_multiple;
    private String is_anonymous;
    private LocalDateTime end_date;
    private LocalDateTime reg_date;
}
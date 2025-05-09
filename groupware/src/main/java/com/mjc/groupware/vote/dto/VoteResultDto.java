package com.mjc.groupware.vote.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VoteResultDto {
    private Long result_no;
    private Long vote_no;
    private Long option_no;
    private Long member_no;
    private LocalDateTime vote_time;
}

package com.mjc.groupware.vote.dto;


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
public class VoteAlarmDto {
    private Long voteNo;
    private String title;
    private String message;
    private String senderName;
    private Long boardNo;
}

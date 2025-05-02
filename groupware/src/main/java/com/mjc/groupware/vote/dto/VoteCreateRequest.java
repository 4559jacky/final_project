package com.mjc.groupware.vote.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteCreateRequest {
    private VoteDto voteDto;
    private List<VoteOptionDto> options;
}
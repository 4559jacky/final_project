//package com.mjc.groupware.vote.service;
//
//import java.util.List;
//
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import com.mjc.groupware.vote.dto.VoteAlarmDto;
//import com.mjc.groupware.vote.entity.Vote;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class VoteAlarmService {
//	
//	private final SimpMessagingTemplate messagingTemplate;
//	
//	public void sendAlarmVoteMembers(List<Long> memberNos, Vote vote, String message) {
//		VoteAlarmDto dto = VoteAlarmDto.builder()
//			    .voteNo(vote.getVoteNo())
//			    .title("투표 마감 알림")
//			    .message(vote.getBoard().getMember().getMemberName() + "님의 투표가 마감되었습니다.")
//			    .creatorName(vote.getBoard().getMember().getMemberName())
//			    .build();
//		
//		for(Long memberNo : memberNos) {
//			messagingTemplate.convertAndSend("/topic/vote/alarm/" + memberNo, dto);
//		}
//	}
//}

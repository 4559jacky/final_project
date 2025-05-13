//package com.mjc.groupware.vote.dto;
//
//import java.time.LocalDateTime;
//
//import com.mjc.groupware.vote.entity.VoteAlarm;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@ToString
//public class VoteAlarmDto {
//	private Long alarm_no;
//    private Long vote_no;
//    private String message;
//    private Boolean is_read;
//    private LocalDateTime created_alarm;
//
//    public static VoteAlarmDto fromEntity(VoteAlarm alarm) {
//        return new VoteAlarmDto(
//                alarm.getAlarmNo(),
//                alarm.getVote().getVoteNo(),
//                alarm.getMessage(),
//                alarm.getIsRead(),
//                alarm.getCreatedAlarm());
//    }
//}
//

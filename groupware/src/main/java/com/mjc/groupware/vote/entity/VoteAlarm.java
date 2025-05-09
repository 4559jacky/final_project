//package com.mjc.groupware.vote.entity;
//
//import java.time.LocalDateTime;
//
//import com.mjc.groupware.member.entity.Member;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Table(name = "vote_alarm")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class VoteAlarm {
//	
//	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "alarm_no")
//    private Long alarmNo;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "vote_no")
//    private Vote vote;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_no")
//    private Member member;
//
//    @Column(name = "message")
//    private String message;
//
//    @Column(name = "is_read")
//    private Boolean isRead = false;
//
//    @Column(name = "created_alarm")
//    private LocalDateTime createdAlarm = LocalDateTime.now();
//}
//

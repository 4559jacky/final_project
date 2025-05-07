package com.mjc.groupware.chat.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="chat_alarm")
@Builder
@Getter
@Setter
public class ChatAlarm {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="chat_alarm_no")
	private Long chatAlarmNo;

	@ManyToOne
	@JoinColumn(name="member_no")
	private Member memberNo;
	
	@Builder.Default
	@Column(name="read_status")
	private String readStatus = "N";
	
	@Column(name="read_date")
	private LocalDateTime readDate;
	
	@CreationTimestamp
	@Column(updatable=false, name="reg_date")
	private LocalDateTime regDate;

	@ManyToOne
	@JoinColumn(name="chat_msg_no")
    private ChatMsg chatMsgNo; 

	
}

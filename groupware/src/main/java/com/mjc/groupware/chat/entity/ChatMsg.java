package com.mjc.groupware.chat.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name="chat_msg")
@Builder
@Getter
@Setter
public class ChatMsg {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="chat_msg_no")
	private Long chatMsgNo;
	
	@Column(name="chat_msg_content")
	private String chatMsgContent;

	@Column(name="send_date")
	private LocalDateTime sendDate;
	
	@Column(name="check")
	private String check;
	
	@ManyToOne
	@JoinColumn(name="chat_room_no")
	private ChatRoom chatRoomNo;
	
	@ManyToOne
	@JoinColumn(name="member_no")
	private Member memberNo;
	
}

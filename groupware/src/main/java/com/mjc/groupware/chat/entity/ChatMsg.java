package com.mjc.groupware.chat.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="chat_msg")
@Builder
@Getter
@Setter
@ToString
public class ChatMsg {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="chat_msg_no")
	private Long chatMsgNo;
	
	@Column(name="chat_msg_content")
	private String chatMsgContent;

	@CreationTimestamp
	@Column(updatable = false, name="send_date")
	private LocalDateTime sendDate;
	
	@ManyToOne
	@JoinColumn(name="chat_room_no")
	private ChatRoom chatRoomNo;
	
	@ManyToOne
	@JoinColumn(name="member_no")
	private Member memberNo;
	
	
	@Column(name="chat_msg_type")
	private String chatMsgType;
	
	@ManyToOne
	@JoinColumn(name="attach_no")
	private ChatAttach attachNo;
}

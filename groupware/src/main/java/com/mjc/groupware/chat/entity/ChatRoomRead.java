package com.mjc.groupware.chat.entity;

import java.time.LocalDateTime;
import java.util.List;

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

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="chat_room_read")
@Builder
@Getter
@Setter
public class ChatRoomRead {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="chat_room_read_no")
	private Long chatRoomReadNo;
	
	@ManyToOne
	@JoinColumn(name="chat_room_no")
	private ChatRoom chatRoomNo;

	@ManyToOne
	@JoinColumn(name="member_no")
	private Member memberNo;	
	
	@Column(name="last_read_time")
	private LocalDateTime lastReadTime;
}

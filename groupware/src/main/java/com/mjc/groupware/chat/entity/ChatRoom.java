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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="chat_room")
@Builder
@Getter
@Setter
public class ChatRoom {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="chat_room_no")
	private Long chatRoomNo;
	
	@Column(name="chat_room_title")
	private String chatRoomTitle;

	@ManyToOne
	@JoinColumn(name="member_no")
	private Member memberNo;	// 생성자 
	
	@Column(name="last_msg")
	private String lastMsg;
	
	@Column(name="last_msg_date")
	private LocalDateTime lastMsgDate;
	
	@CreationTimestamp
	@Column(updatable=false, name="reg_date")
	private LocalDateTime regDate;
	
	@Builder.Default
	@Column(name="chat_room_status")
	private String chatRoomstatus = "Y";

	@OneToMany(mappedBy = "chatRoomNo")
    private List<ChatMapping> mappings; 

	
	
}

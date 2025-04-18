package com.mjc.groupware.chat.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
	private String chatRoomtitle;

	@ManyToOne
	@JoinColumn(name="member_no")
	private Member memberNo;	
	
	@Column(name="last_message")
	private String lastMessage;
	
	@Column(name="last_message_date")
	private LocalDateTime lastMessageDate;
	
	@Column(name="reg_date")
	private LocalDateTime regDate;
	
	@ManyToMany
    @JoinTable(
        name = "chat_mapping",
        joinColumns = @JoinColumn(name = "chat_room_no"),
        inverseJoinColumns = @JoinColumn(name = "member_no")
    )
    private List<ChatMapping> mappings = new ArrayList<>(); 
	
	
}

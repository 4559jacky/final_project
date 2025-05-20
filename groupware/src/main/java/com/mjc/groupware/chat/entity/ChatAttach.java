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
import lombok.ToString;

@ToString
@Entity
@Table(name="chat_attach")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatAttach {
 
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long attachNo;
	
	@Column(name="ori_name")
	private String oriName;
	
	@Column(name="new_name")
	private String newName;
	
	@Column(name="attach_path")
	private String attachPath;;
	
	@ManyToOne
	@JoinColumn(name="chat_room_no")
	private ChatRoom chatRoomNo;
	
	@ManyToOne
	@JoinColumn(name="member_no")
	private Member memberNo;
	
	@CreationTimestamp
	@Column(updatable = false, name="send_date")
	private LocalDateTime sendDate;
	
	@Column(name = "file_size")
	private Long fileSize;


}

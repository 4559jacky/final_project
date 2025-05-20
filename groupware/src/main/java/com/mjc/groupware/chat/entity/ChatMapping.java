package com.mjc.groupware.chat.entity;

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
@Table(name="chat_mapping")
@Builder
@Getter
@Setter
public class ChatMapping {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="mapping_no")
	private Long mappingNo;

	@ManyToOne
	@JoinColumn(name="chat_room_no")
	private ChatRoom chatRoomNo;	
	
	@ManyToOne
	@JoinColumn(name="member_no")
	private Member memberNo;	
	
	@Builder.Default
	@Column(name="member_status")
	private String memberStatus = "Y";
}

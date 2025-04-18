package com.mjc.groupware.chat.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ChatMsgDto {

	 private Long chat_msg_no;
	 
	 private String chat_msg_content;
	 
	 private LocalDateTime send_date;
	 
	 private String check;
	 
	 private Long chat_room_no;
	 
	private Long member_no;
	
	private Long mapping_no;
}

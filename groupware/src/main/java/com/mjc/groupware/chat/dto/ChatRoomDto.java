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
public class ChatRoomDto {
	
	private Long chat_room_no;

	private String chat_room_title;
	
	private Long member_no;
	
	private String member_name;
	
	private String member_pos_name;
	
	private String member_dept_name;
	
	private String last_msg;
	
	private LocalDateTime last_msg_date;
	
	private LocalDateTime reg_date;
}

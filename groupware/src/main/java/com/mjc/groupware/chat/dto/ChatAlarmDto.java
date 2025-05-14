package com.mjc.groupware.chat.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.chat.entity.ChatMsg;

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
public class ChatAlarmDto {
	
	private Long chat_alarm_no;
	
	private Long member_no;
	
	private Long chat_room_no;
	
	private String chat_room_name;
	
	private String read_status;
	
	private LocalDateTime read_date;
	
	private LocalDateTime reg_date;
	
	private Long chat_msg_no;
	
	private String chat_msg_content;
	
	private String chat_mag_type;

	private String sender_no;
	
	private String sender_name;
	
	private String sender_pos_name;
	
	private String sender_dept_name;
	
    private String chat_alarm_content;
    
    private String chat_room_title;
	

}

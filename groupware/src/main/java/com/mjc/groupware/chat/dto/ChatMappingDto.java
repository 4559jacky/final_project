package com.mjc.groupware.chat.dto;

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
public class ChatMappingDto {

	private Long mapping_no;
	
	private Long chat_room_no;
	
	private Long member_no;
	
	
}

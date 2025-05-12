package com.mjc.groupware.chat.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.chat.entity.ChatAttach;
import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.member.entity.Member;

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
public class ChatAttachDto {

	private Long attach_no;
	private String ori_name;
	private String new_name;
	private String attach_path;
	private Long chat_room_no;
	private Long member_no;
	private String member_name;
	private String member_pos_name;
	private String member_dept_name;
	private LocalDateTime send_date;
	private Long file_size;
	private String file_size_str;
	
	public ChatAttach toEntity() {
		return ChatAttach.builder()
				.attachNo(attach_no)
				.oriName(ori_name)
				.newName(new_name)
				.attachPath(attach_path)
				.chatRoomNo(ChatRoom.builder().chatRoomNo(chat_room_no).build())
				.memberNo(Member.builder().memberNo(member_no).build())
				.sendDate(send_date)
				.fileSize(file_size)
				.build();
	}
	
}

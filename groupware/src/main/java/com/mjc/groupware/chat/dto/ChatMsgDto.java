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
@Builder
public class ChatMsgDto {

	 private Long chat_msg_no;
	 
	 private String chat_msg_content;
	  
	 private LocalDateTime send_date;
	 
	 private String check;
	 
	 private Long chat_room_no;
	 
	 private Long member_no;
	 
	 private String member_name;
	 
	 private String member_pos_name;
	 
	 private String member_dept_name;
	
	 // 변환 메서드 (Entity → DTO)
    public ChatMsgDto toDto(ChatMsg msg) {
        return ChatMsgDto.builder()
                .chat_msg_no(msg.getChatMsgNo())
                .chat_msg_content(msg.getChatMsgContent())
                .send_date(msg.getSendDate()) // sendDate가 String일 경우 변환
                .check(msg.getCheck())
                .chat_room_no(msg.getChatRoomNo().getChatRoomNo())
                .member_no(msg.getMemberNo().getMemberNo())
                .member_name(msg.getMemberNo().getMemberName())
                .member_pos_name(msg.getMemberNo().getPos() != null? msg.getMemberNo().getPos().getPosName() : null)
                .member_dept_name(msg.getMemberNo().getDept()  != null? msg.getMemberNo().getDept().getDeptName() : null)
                .build();
    }
}

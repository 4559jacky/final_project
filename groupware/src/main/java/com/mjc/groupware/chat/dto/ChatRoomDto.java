package com.mjc.groupware.chat.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mjc.groupware.chat.entity.ChatMapping;
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
public class ChatRoomDto {
	
	private Long chat_room_no;

	private String chat_room_title;
	
	private Long create_member_no;
	
	private String member_name;
	
	private String member_pos_name;
	
	private String member_dept_name;
	
	private String last_msg;
	
	private LocalDateTime last_msg_date;
	
	private LocalDateTime reg_date;
	
	private List<Long> member_no;
	
	// selectChatRoonOne 조회용
	private List<ChatMemberInfoDto> memberInfos;
	
	public static ChatRoomDto toDto(ChatRoom room) {
	    List<Long> memberNoList = new ArrayList<>();
	    List<ChatMemberInfoDto> memberInfoList = new ArrayList<>();
	    
	    if (room.getMappings() != null) {
	        for (ChatMapping mapping : room.getMappings()) {
	            Member member = mapping.getMemberNo();
	            if (member != null) {
	                memberNoList.add(member.getMemberNo());

	                ChatMemberInfoDto info = new ChatMemberInfoDto(
	                    member.getMemberNo(),
	                    member.getMemberName(),
	                    member.getPos() != null ? member.getPos().getPosName() : null,
	                    member.getDept() != null ? member.getDept().getDeptName() : null
	                );
	                memberInfoList.add(info);
	            }
	        }
	    }

	    return ChatRoomDto.builder()
	            .chat_room_no(room.getChatRoomNo())
	            .chat_room_title(room.getChatRoomTitle())
	            .create_member_no(room.getCreateMemberNo().getMemberNo())
	            .member_name(room.getCreateMemberNo().getMemberName())
	            .member_pos_name(
	                room.getCreateMemberNo().getPos() != null 
	                    ? room.getCreateMemberNo().getPos().getPosName() 
	                    : null
	            )
	            .member_dept_name(
	                room.getCreateMemberNo().getDept() != null 
	                    ? room.getCreateMemberNo().getDept().getDeptName() 
	                    : null
	            )
	            .last_msg(room.getLastMsg())
	            .last_msg_date(room.getLastMsgDate())
	            .reg_date(room.getRegDate())
	            .member_no(memberNoList)
	            .memberInfos(memberInfoList)
	            .build();
	}

}

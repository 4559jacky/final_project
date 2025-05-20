package com.mjc.groupware.chat.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.entity.ChatRoomRead;
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
public class ChatRoomReadDto {
	
	private Long chat_room_read_no;
	
	private Long chat_room_no;
	
	private Long member_no;
	
	private LocalDateTime last_read_time;
	
	 // 👉 DTO → Entity
    public ChatRoomRead toEntity() {
        return ChatRoomRead.builder()
                .chatRoomReadNo(this.chat_room_read_no)
                .chatRoomNo(ChatRoom.builder().chatRoomNo(this.chat_room_no).build())
                .memberNo(Member.builder().memberNo(this.member_no).build())
                .lastReadTime(this.last_read_time)
                .build();
    }

    // 👉 Entity → DTO
    public static ChatRoomReadDto toDto(ChatRoomRead entity) {
        return ChatRoomReadDto.builder()
                .chat_room_read_no(entity.getChatRoomReadNo())
                .chat_room_no(entity.getChatRoomNo().getChatRoomNo())
                .member_no(entity.getMemberNo().getMemberNo())
                .last_read_time(entity.getLastReadTime())
                .build();
    }
	
}

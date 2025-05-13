package com.mjc.groupware.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mjc.groupware.chat.entity.ChatAttach;
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
@ToString
public class ChatMsgDto {

	 private Long chat_msg_no;
	 
	 private String chat_msg_content;
	  
	 private LocalDateTime send_date;
	 
	 private Long chat_room_no;
	 
	 private Long member_no;
	 
	 private String member_name;
	 
	 private String member_pos_name;
	 
	 private String member_dept_name;
	 
	 private List<Long> member_no_list;
	 
	 private String chat_msg_type;
	 
	 private Long attach_no;
	 
	 private String ori_name;
	 private String new_name;
	 private String attach_path;
	 private Long file_size;
	 private String file_size_str;
	 private String file_url;
	 private String file_type;
	
	 // 변환 메서드 (Entity → DTO)
	 public ChatMsgDto toDto(ChatMsg msg) {
		    ChatAttach attach = msg.getAttachNo();
		    
		    return ChatMsgDto.builder()
		            .chat_msg_no(msg.getChatMsgNo())
		            .chat_msg_content(msg.getChatMsgContent())
		            .send_date(msg.getSendDate())
		            .chat_room_no(msg.getChatRoomNo().getChatRoomNo())
		            .member_no(msg.getMemberNo().getMemberNo())
		            .member_name(msg.getMemberNo().getMemberName())
		            .member_pos_name(msg.getMemberNo().getPos() != null ? msg.getMemberNo().getPos().getPosName() : null)
		            .member_dept_name(msg.getMemberNo().getDept() != null ? msg.getMemberNo().getDept().getDeptName() : null)
		            .chat_msg_type(msg.getChatMsgType())
		            .attach_no(attach != null ? attach.getAttachNo() : null)
		            .ori_name(attach != null ? attach.getOriName() : null)
		            .new_name(attach != null ? attach.getNewName() : null)
		            .attach_path(attach != null ? attach.getAttachPath() : null)
		            .file_size(attach != null ? attach.getFileSize() : null)
		            .file_size_str(attach != null ? formatFileSize(attach.getFileSize()) : null)
		            .file_url(attach != null ? "/upload/groupware/chat/" + attach.getNewName() : null)
		            .build();
		}

	 
	 private String formatFileSize(Long bytes) {
		    if (bytes == null) return "";
		    if (bytes < 1024) return bytes + " B";
		    int unit = 1024;
		    int exp = (int) (Math.log(bytes) / Math.log(unit));
		    String pre = "KMGTPE".charAt(exp - 1) + "";
		    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
		}



}

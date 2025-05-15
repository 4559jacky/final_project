package com.mjc.groupware.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mjc.groupware.chat.dto.ChatAlarmDto;
import com.mjc.groupware.chat.entity.ChatAlarm;
import com.mjc.groupware.chat.entity.ChatMapping;
import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.repository.ChatAlarmRepository;
import com.mjc.groupware.chat.specification.ChatAlarmSpecification;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ChatAlarmService {

    private final ChatAlarmRepository chatAlarmRepository;
    private final MemberRepository memberRepository;
    
    
    public void createChatAlarm(Long chatRoomNo, Long receiverNo, ChatMsg chatMsg) {
        Member receiver = memberRepository.findById(receiverNo)
            .orElseThrow(() -> new RuntimeException("❌ 수신자 멤버 없음"));

        // ❌ 시스템 메시지는 알림 저장 X
        if ("Y".equals(chatMsg.getChatMsgType())) return;

        String alarmContent;

        if ("FILE".equals(chatMsg.getChatMsgType()) && chatMsg.getAttachNo() != null) {
            String oriName = chatMsg.getAttachNo().getOriName();
            String ext = oriName != null ? oriName.substring(oriName.lastIndexOf('.') + 1).toLowerCase() : "";
            boolean isImage = List.of("png", "jpg", "jpeg", "gif", "bmp", "webp").contains(ext);

            alarmContent = chatMsg.getMemberNo().getMemberName() + "님이 "
                         + (isImage ? "이미지를 전송했습니다." : "파일을 전송했습니다.");
        } else {
            // 📩 일반 메시지는 내용 그대로!
            alarmContent = chatMsg.getChatMsgContent();
        }

        ChatAlarm alarm = ChatAlarm.builder()
            .memberNo(receiver)
            .chatMsgNo(chatMsg)
            .readStatus("N")
            .chatAlarmContent(alarmContent)
            .build();

        chatAlarmRepository.save(alarm);
    }


    
	public List<ChatAlarmDto> selectChatAlarmAll(Long memberNo) {
		 Member member = memberRepository.findById(memberNo).orElseThrow();
		    
		    Specification<ChatAlarm> spec = ChatAlarmSpecification.unreadByMember(member);
		    List<ChatAlarm> alarmList = chatAlarmRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "regDate"));

		    List<ChatAlarmDto> result = new ArrayList<>();

		    for (ChatAlarm alarm : alarmList) {
		        ChatMsg msg = alarm.getChatMsgNo();
		        ChatRoom room = msg.getChatRoomNo();
		        Member sender = msg.getMemberNo(); // 메세지 보낸 사람

		        // ✅ 채팅방 제목 처리
		        String title = room.getChatRoomTitle();
		        if (title == null || title.trim().isEmpty()) {
		            List<ChatMapping> mappings = room.getMappings();
		            StringBuilder titleBuilder = new StringBuilder();

		            for (ChatMapping mapping : mappings) {
		                Member other = mapping.getMemberNo();
		                if (!other.getMemberNo().equals(memberNo) && "Y".equals(mapping.getMemberStatus())) {
		                    String pos = other.getPos() != null ? other.getPos().getPosName() : "";
		                    if (titleBuilder.length() > 0) titleBuilder.append(", ");
		                    titleBuilder.append(other.getMemberName()).append(" ").append(pos);
		                }
		            }

		            if (titleBuilder.length() == 0) {
		                title = "(알 수 없음)";
		            } else {
		                title = titleBuilder.toString();
		            }
		        }

		        ChatAlarmDto dto = ChatAlarmDto.builder()
		            .chat_alarm_no(alarm.getChatAlarmNo())
		            .chat_room_no(room.getChatRoomNo())
		            .chat_room_name(title)
		            .chat_msg_no(msg.getChatMsgNo())
		            .chat_msg_content(msg.getChatMsgContent())
		            .chat_alarm_content(alarm.getChatAlarmContent())
		            .member_no(sender.getMemberNo())
		            .sender_name(sender.getMemberName())
		            .sender_pos_name(sender.getPos() != null ? sender.getPos().getPosName() : "")
		            .sender_dept_name(sender.getDept() != null ? sender.getDept().getDeptName() : "")
		            .build();

		        result.add(dto);
		    }

		    return result;
	}
    

}

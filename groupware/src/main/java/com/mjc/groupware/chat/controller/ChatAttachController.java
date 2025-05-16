package com.mjc.groupware.chat.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.chat.dto.ChatAttachDto;
import com.mjc.groupware.chat.dto.ChatMsgDto;
import com.mjc.groupware.chat.entity.ChatAttach;
import com.mjc.groupware.chat.entity.ChatMapping;
import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.repository.ChatMsgRepository;
import com.mjc.groupware.chat.repository.ChatRoomRepository;
import com.mjc.groupware.chat.service.ChatAlarmService;
import com.mjc.groupware.chat.service.ChatAttachService;
import com.mjc.groupware.chat.service.ChatRoomService;
import com.mjc.groupware.chat.session.ChatSessionTracker;
import com.mjc.groupware.chat.session.SessionRegistry;
import com.mjc.groupware.common.annotation.CheckPermission;
import com.mjc.groupware.member.entity.Member;

import lombok.RequiredArgsConstructor;
 
@Controller
@RequiredArgsConstructor
public class ChatAttachController {
 
	private final ChatAttachService attachService;
	private final ChatRoomService chatRoomService;
	private final ChatMsgRepository chatMsgRepository;
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatRoomRepository chatRoomRepository;
	
	private final ChatAlarmService chatAlarmService;
    private final SessionRegistry sessionRegistry; 
    private final ChatSessionTracker chatSessionTracker; 
	
	// 파일 업로드 
    @CheckPermission("CHAT_USER")
    @PostMapping("/chat/attach/create")
    @ResponseBody
    public Map<String, String> createAttachApi(@RequestParam("files") List<MultipartFile> files,
                                               @RequestParam("chatRoomNo") Long roomNo,
                                               @RequestParam("memberNo") Long memberNo) {

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "파일 등록 중 오류가 발생하였습니다.");

        try {
            ChatRoom chatRoom = chatRoomService.selectChatRoomOne(roomNo);
            List<ChatMapping> mappings = chatRoom.getMappings();

            Member sender = mappings.stream()
                    .map(ChatMapping::getMemberNo)
                    .filter(m -> m.getMemberNo().equals(memberNo))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("멤버 매핑 없음"));

            for (MultipartFile mf : files) {
                ChatAttachDto dto = attachService.uploadFile(mf);

                if (dto != null) {
                    dto.setChat_room_no(roomNo);
                    dto.setMember_no(memberNo);
                    dto.setFile_size(mf.getSize());

                    // ✅ MIME 타입 추출 및 저장
                    String mimeType = getMimeType(dto.getAttach_path());

                    Long savedAttachNo = attachService.createAttach(dto);
                    ChatAttach attachEntity = attachService.findById(savedAttachNo);

                    String content = mimeType != null && mimeType.startsWith("image/")
                            ? sender.getMemberName() + "님이 이미지를 전송했습니다."
                            : sender.getMemberName() + "님이 파일을 전송했습니다.";

                    ChatMsg fileMsg = ChatMsg.builder()
                            .chatRoomNo(chatRoom)
                            .memberNo(sender)
                            .chatMsgContent(null)
                            .chatMsgType("FILE")
                            .attachNo(attachEntity)
                            .sendDate(LocalDateTime.now())
                            .build();

                    ChatMsg savedMsg = chatMsgRepository.save(fileMsg);

                    // 채팅방 마지막 메시지 갱신
                    chatRoom.setLastMsg(content);
                    chatRoom.setLastMsgDate(LocalDateTime.now());
                    chatRoomRepository.save(chatRoom);

                    // 채팅방 내부 전송
                    Map<String, Object> innerMsg = new HashMap<>();
                    innerMsg.put("chat_msg_no", savedMsg.getChatMsgNo());
                    innerMsg.put("chat_room_no", roomNo);
                    innerMsg.put("member_no", memberNo);
                    innerMsg.put("msg_type", "FILE");
                    innerMsg.put("chat_msg_content", null);
                    innerMsg.put("file_name", dto.getOri_name());
                    innerMsg.put("file_url", "/chat/download/" + savedAttachNo);
                    innerMsg.put("file_type", mimeType);
                    innerMsg.put("send_date", savedMsg.getSendDate().toString());
                    innerMsg.put("member_name", sender.getMemberName());
                    innerMsg.put("member_pos_name", sender.getPos().getPosName());
                    innerMsg.put("member_dept_name", sender.getDept().getDeptName());
                    innerMsg.put("file_size", formatFileSize(dto.getFile_size()));

                    messagingTemplate.convertAndSend("/topic/chat/room/" + roomNo, innerMsg);

                    // 목록/알림용 DTO
                    ChatMsgDto updateDto = new ChatMsgDto();
                    updateDto.setChat_msg_no(savedMsg.getChatMsgNo());
                    updateDto.setChat_room_no(roomNo);
                    updateDto.setMember_no(memberNo);
                    updateDto.setChat_msg_content(content);
                    updateDto.setSend_date(savedMsg.getSendDate());
                    updateDto.setChat_msg_type("FILE");
                    updateDto.setMember_name(sender.getMemberName());
                    updateDto.setMember_pos_name(sender.getPos().getPosName());
                    updateDto.setMember_dept_name(sender.getDept().getDeptName());
                    updateDto.setFile_url("/chat/download/" + savedAttachNo);
                    updateDto.setFile_size_str(formatFileSize(dto.getFile_size()));

                    // 수신자 설정
                    List<Long> receiverNos = mappings.stream()
                            .map(m -> m.getMemberNo().getMemberNo())
                            .filter(no -> !no.equals(memberNo))
                            .collect(Collectors.toList());
                    updateDto.setMember_no_list(receiverNos);

                    // 목록 갱신
                    messagingTemplate.convertAndSend("/topic/chat/room/update", updateDto);

                    // 뱃지 + 알림
                    for (Long receiverNo : receiverNos) {
                        String sessionId = sessionRegistry.getSessionIdForMember(receiverNo);
                        boolean isConnected = (sessionId != null);
                        boolean isInRoom = isConnected && chatSessionTracker.isSessionInRoom(roomNo, sessionId);

                        if (isConnected && !isInRoom) {
                            messagingTemplate.convertAndSend("/topic/chat/unread", updateDto);
                        }

                        if (!isInRoom) {
                            chatAlarmService.createChatAlarm(roomNo, receiverNo, savedMsg);

                         // ✅ 알림 내용
                            String alarmContent = mimeType != null && mimeType.startsWith("image/")
                                    ? sender.getMemberName() + "님이 이미지를 전송했습니다."
                                    : sender.getMemberName() + "님이 파일을 전송했습니다.";

                            // ✅ 방 이름 구성
                            String displayTitle = chatRoomService.getChatRoomDisplayTitle(chatRoom,memberNo);

                            // ✅ 실시간 알림 DTO 구성
                            ChatMsgDto alarmDto = new ChatMsgDto();
                            alarmDto.setMember_no(receiverNo);
                            alarmDto.setMember_name(sender.getMemberName());
                            alarmDto.setMember_pos_name(sender.getPos().getPosName());
                            alarmDto.setChat_msg_content(content);
                            alarmDto.setChat_room_no(roomNo);
                            alarmDto.setChat_alarm_content(alarmContent); // 알림 메시지
                            alarmDto.setChat_room_title(displayTitle);  // 알림에 쓸 방 이름

                            messagingTemplate.convertAndSend("/topic/chat/alarm/" + receiverNo, alarmDto);

                        }
                    }
                }
            }

            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "파일이 업로드되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }




		 // 1. 파일 타입 추출 함수
	    private String getMimeType(String path) {
	        try {
	            return Files.probeContentType(Paths.get(path));
	        } catch (Exception e) {
	            return "application/octet-stream";
	        }
	    }

	    // 2. 파일 크기 포맷 함수
	    private String formatFileSize(Long bytes) {
	        if (bytes == null) return "";
	        if (bytes < 1024) return bytes + " B";
	        int unit = 1024;
	        int exp = (int) (Math.log(bytes) / Math.log(unit));
	        String pre = "KMGTPE".charAt(exp - 1) + "";
	        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	    }
	
	    
	    @CheckPermission("CHAT_USER")
	    @GetMapping("/chat/download/{id}")
		public ResponseEntity<Object> fileDownlod(@PathVariable("id") Long id) {
			try {
				ChatAttach fileData = attachService.selectAttachOne(id);
				
				// 파일이 없으면 notFound 에러 발생시킴
				if(fileData == null) return ResponseEntity.notFound().build();
				
				Path filePath = Paths.get(fileData.getAttachPath());
				Resource resource = new InputStreamResource(Files.newInputStream(filePath));
				
				String oriFileName = fileData.getOriName();
				String encodedName = URLEncoder.encode(oriFileName, StandardCharsets.UTF_8);
				
				HttpHeaders headers = new HttpHeaders();
				headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+encodedName);
				
				return new ResponseEntity<Object>(resource,headers,HttpStatus.OK);

			} catch(Exception e) {
	 			e.printStackTrace();
	 			return ResponseEntity.badRequest().build();
	 		}
			
			
		}
}

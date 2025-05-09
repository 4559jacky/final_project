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
import com.mjc.groupware.chat.entity.ChatAttach;
import com.mjc.groupware.chat.entity.ChatMapping;
import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.repository.ChatMsgRepository;
import com.mjc.groupware.chat.service.ChatAttachService;
import com.mjc.groupware.chat.service.ChatRoomService;
import com.mjc.groupware.member.entity.Member;

import lombok.RequiredArgsConstructor;
 
@Controller
@RequiredArgsConstructor
public class ChatAttachController {
 
	private final ChatAttachService attachService;
	private final ChatRoomService chatRoomService;
	private final ChatMsgRepository chatMsgRepository;
	private final SimpMessagingTemplate messagingTemplate;
	
	// 파일 업로드 
	@PostMapping("/chat/attach/create")
	@ResponseBody
	public Map<String,String> createAttachApi(@RequestParam("files") List<MultipartFile> files,
	                                          @RequestParam("chatRoomNo") Long roomNo,
	                                          @RequestParam("memberNo") Long memberNo) {

	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "파일 등록 중 오류가 발생하였습니다.");

	    try {
	        ChatRoom chatRoom = chatRoomService.selectChatRoomOne(roomNo);
	        List<ChatMapping> mappings = chatRoom.getMappings();

	        // 업로더 정보 조회
	        Member sender = mappings.stream()
	            .map(ChatMapping::getMemberNo)
	            .filter(m -> m.getMemberNo().equals(memberNo))
	            .findFirst()
	            .orElseThrow(() -> new RuntimeException("멤버 매핑 없음"));

	        for (MultipartFile mf : files) {
	            ChatAttachDto dto = attachService.uploadFile(mf);

	            if (dto != null) {
	                // 1. 파일 DB 저장
	                dto.setChat_room_no(roomNo);
	                dto.setMember_no(memberNo);
	                dto.setFile_size(mf.getSize());
	                Long savedAttachNo = attachService.createAttach(dto); // PK 반환되도록 설정 필요

	                // 2. chatMsg 생성 시 attach_no FK로 연결
	                ChatAttach attachEntity = attachService.findById(savedAttachNo); // 방금 저장한 attach 엔티티

	                ChatMsg fileMsg = ChatMsg.builder()
	                    .chatRoomNo(chatRoom)
	                    .memberNo(sender)
	                    .chatMsgContent(null)
	                    .chatMsgType("FILE")
	                    .attachNo(attachEntity) // 🔥 여기!
	                    .sendDate(LocalDateTime.now())
	         
	                    .build();

	                ChatMsg savedMsg = chatMsgRepository.save(fileMsg);

	                // 3. WebSocket 메시지 전송
	                Map<String, Object> socketMsg = new HashMap<>();
	                socketMsg.put("chat_msg_no", savedMsg.getChatMsgNo());
	                socketMsg.put("chat_room_no", roomNo);
	                socketMsg.put("member_no", memberNo);
	                socketMsg.put("msg_type", "FILE");
	                socketMsg.put("file_name", dto.getOri_name());
	                socketMsg.put("file_url", "/chat/download/" + savedAttachNo);
	                socketMsg.put("file_type", getMimeType(dto.getAttach_path()));
	                socketMsg.put("send_date", savedMsg.getSendDate().toString());
	                socketMsg.put("member_name", sender.getMemberName());
	                socketMsg.put("member_pos_name", sender.getPos().getPosName());
	                socketMsg.put("member_dept_name", sender.getDept().getDeptName());
	                socketMsg.put("file_size", formatFileSize(dto.getFile_size()));

	                messagingTemplate.convertAndSend("/topic/chat/room/" + roomNo, socketMsg);
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

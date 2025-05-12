package com.mjc.groupware.chat.service;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.chat.dto.ChatAttachDto;
import com.mjc.groupware.chat.entity.ChatAttach;
import com.mjc.groupware.chat.repository.ChatAttachRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatAttachService {

	@Value("${ffupload.location}")
	private String fileDir;
	
	private final ChatAttachRepository attachRepository;
	
	public ChatAttachDto uploadFile(MultipartFile file) {
	    ChatAttachDto dto = new ChatAttachDto();
	    try {
	        if (file == null || file.isEmpty()) {
	            throw new Exception("존재하지 않는 파일입니다.");
	        }

	        long fileSize = file.getSize();
	        if (fileSize >= 1048576) {
	            throw new Exception("허용 용량을 초과한 파일입니다.");
	        }

	        String oriName = file.getOriginalFilename();
	        dto.setOri_name(oriName);

	        String fileExt = oriName.substring(oriName.lastIndexOf("."), oriName.length());
	        String uniqueName = UUID.randomUUID().toString().replaceAll("-", "");
	        String newName = uniqueName + fileExt;
	        dto.setNew_name(newName);

	        // ✅ 1. chat 폴더 경로 따로 선언
	        String dirPath = fileDir + "chat/";
	        File dir = new File(dirPath);
	        if (!dir.exists()) {
	            dir.mkdirs(); // ✅ chat 폴더 없으면 생성
	        }

	        // ✅ 2. 저장 경로 생성 및 DTO에 저장
	        String downDir = dirPath + newName;
	        dto.setAttach_path(downDir);

	        // ✅ 3. 파일 저장
	        File saveFile = new File(downDir);
	        file.transferTo(saveFile);

	    } catch (Exception e) {
	        dto = null;
	        e.printStackTrace();
	    }
	    return dto;
	}

	
	// 2. 파일 메타 데이터 insert
		public Long createAttach(ChatAttachDto dto) {
		    ChatAttach attach = dto.toEntity();
		    ChatAttach saved = attachRepository.save(attach);
		    dto.setAttach_no(saved.getAttachNo());
		    return saved.getAttachNo(); // ✅ attach_no 반환
		}

		
		public ChatAttach findById(Long id) {
		    return attachRepository.findById(id)
		        .orElseThrow(() -> new RuntimeException("첨부파일 정보 없음"));
		}

		
		
		public ChatAttach selectAttachOne(Long id) {
			return attachRepository.findById(id).orElse(null);
		}

		public List<ChatAttach> selectAttachList() {
			return attachRepository.findAll();
		}
		
	
}

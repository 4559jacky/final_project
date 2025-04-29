package com.mjc.groupware.shared.controller;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.shared.service.SharedFileService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SharedFileController {
	
	private final SharedFileService sharedFileService;
	
	// 공유문서함 메인화면 view
	@GetMapping("/shared")
	public String mainView() {
		return "/shared/main";
	}
	
	// 파일 업로드
	@PostMapping("/shared/files/upload")
	@ResponseBody
	public Map<String, String> uploadFiles(@RequestParam("files") List<MultipartFile> files,
										   @RequestParam("folderId") Long folderId){
		sharedFileService.saveFiles(files,folderId);
		
		return Map.of("message","파일이 성공적으로 업로드 되었습니다.");
	}
	
	// 파일/목록 리스트
	@GetMapping("/shared/files/list")
	@ResponseBody
	public List<Map<String, Object>>getFolderContent(@RequestParam(value = "folderId", required = false) Long folderId){
		return sharedFileService.getFolderContent(folderId);
	}
	
	// 파일 다운로드
	@GetMapping("/shared/files/download/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") Long fileId){
		return sharedFileService.downloadFile(fileId);
	}
	
}

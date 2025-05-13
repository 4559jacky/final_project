package com.mjc.groupware.shared.controller;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.security.MemberDetails;
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
										   @RequestParam("folderId") Long folderId,
										   @AuthenticationPrincipal MemberDetails memberDetails	){
		
	    Member member = memberDetails.getMember();
		sharedFileService.saveFiles(files,folderId,member);
		
		return Map.of("message","파일이 성공적으로 업로드 되었습니다.");
	}
	
	@GetMapping("/shared/files/list")
	@ResponseBody
	public  ResponseEntity<Map<String, Object>> getFolderContent(
	        @RequestParam(value = "folderId", required = false) Long folderId,
	        @RequestParam(value = "folderIds", required = false) List<Long> folderIds,
	        @RequestParam("type") String type,
	        Authentication auth) {

	    MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
	    Member member = memberDetails.getMember();

	    try {
	        Map<String, Object> result = sharedFileService.getFolderContent(folderId, folderIds, member, type);
	        return ResponseEntity.ok(result);
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(403).body(Map.of("error", "접근 권한이 없습니다."));
	    }
	}
	
	// 파일 다운로드
	@GetMapping("/shared/files/download/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") Long fileId){
		return sharedFileService.downloadFile(fileId);
	}
	
	@PostMapping("/shared/file/move")
	@ResponseBody
	public Map<String, String> moveFile(@RequestBody Map<String, Long> payload) {
	    Long fileId = payload.get("fileId");
	    Long newFolderId = payload.get("newFolderId");
	    sharedFileService.moveFile(fileId, newFolderId);
	    return Map.of("message", "파일 위치가 변경되었습니다.");
	}
	
	@PostMapping("/shared/file/delete")
	@ResponseBody
	public Map<String, Object> deleteFile(@RequestBody Map<String, Long> payload, Authentication auth) {
	    Long fileId = payload.get("id");
	    Member member = ((MemberDetails) auth.getPrincipal()).getMember();

	    sharedFileService.softDelete(fileId, member.getMemberNo());

	    return Map.of("message", "파일이 삭제되었습니다.");
	}
	
}

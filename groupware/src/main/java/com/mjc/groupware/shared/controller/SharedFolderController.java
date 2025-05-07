package com.mjc.groupware.shared.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.shared.dto.SharedFolderDto;
import com.mjc.groupware.shared.entity.SharedFile;
import com.mjc.groupware.shared.entity.SharedFolder;
import com.mjc.groupware.shared.repository.FileRepository;
import com.mjc.groupware.shared.repository.FolderRepository;
import com.mjc.groupware.shared.service.SharedFolderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SharedFolderController {
	
	private final FolderRepository folderRepository;
	private final SharedFolderService sharedFolderService;
	private final FileRepository fileRepository;
	
	@GetMapping("/shared/main/tree")
	@ResponseBody
	public List<Map<String, Object>> getFolderTree(Authentication auth) {
	    MemberDetails memberDetails = (MemberDetails) auth.getPrincipal(); // (1) 로그인한 사용자 정보 (MemberDetails)
	    Member member = memberDetails.getMember(); // 진짜 Member 꺼내기

	    Long deptNo = (member.getDept() != null) ? member.getDept().getDeptNo() : null;

	    List<SharedFolder> folders;
	    if (deptNo == null) {
	        folders = folderRepository.findSharedFolders();
	    } else {
	        folders = folderRepository.findByAccessControl(member.getMemberNo(), deptNo);
	    }

	    List<Map<String, Object>> result = new ArrayList<>();

	    for (SharedFolder folder : folders) {
	        // ✅ 폴더 노드 추가 (기존 코드 그대로)
	        Map<String, Object> node = new HashMap<>();
	        node.put("id", folder.getFolderNo());
	        node.put("parent", folder.getParentFolder() == null ? "#" : folder.getParentFolder().getFolderNo());
	        node.put("text", folder.getFolderName());
	        node.put("icon", "jstree-folder");
	        result.add(node);

	        // ✅ 파일 노드 추가 (여기만 추가된 부분)
	        List<SharedFile> files = fileRepository.findByFolderFolderNo(folder.getFolderNo()).stream()
	            .filter(file -> "N".equals(file.getFileStatus()))
	            .toList();

	        for (SharedFile file : files) {
	            Map<String, Object> fileNode = new HashMap<>();
	            fileNode.put("id", "file-" + file.getFileNo());
	            fileNode.put("parent", folder.getFolderNo());
	            fileNode.put("text", file.getFileName());
	            fileNode.put("icon", "jstree-file");
	            result.add(fileNode);
	        }
	    }

	    return result;
	}
	
	// 최상위,하위 폴더 생성.
	@PostMapping("/shared/folder/create")
	@ResponseBody
	public Map<String,String> createFolder(@RequestBody SharedFolderDto dto){
		sharedFolderService.createFolder(dto);
		return Map.of("message", "📁 폴더가 생성되었습니다.");
	}
	
	// 부모 폴더 타입 상속.
	@GetMapping("/shared/folder/type")
	@ResponseBody
	public Map<String, Integer> getFolderType(@RequestParam("folderId") Long folderId) {
	    SharedFolder folder = folderRepository.findById(folderId)
	        .orElseThrow(() -> new RuntimeException("상위 폴더를 찾을 수 없습니다."));
	    return Map.of("folderType", folder.getFolderType());
	}
	
	// 폴더 이동
	@PostMapping("/shared/folder/move")
	@ResponseBody
	public Map<String, String> moveFolder(@RequestBody Map<String, Long> payload) {
	    Long folderId = payload.get("folderId");
	    Long newParentId = payload.get("newParentId");
	    sharedFolderService.moveFolder(folderId, newParentId);
	    return Map.of("message", "폴더 위치가 변경되었습니다.");
	}
	
	@PostMapping("/shared/folder/delete")
	@ResponseBody
	public Map<String, Object> deleteFolder(@RequestBody Map<String, Long> payload, Authentication auth) {
	    Long folderId = payload.get("id");
	    Member member = ((MemberDetails) auth.getPrincipal()).getMember();

	    sharedFolderService.softDelete(folderId, member.getMemberNo());

	    return Map.of("message", "폴더가 삭제되었습니다.");
	}
	
	
}

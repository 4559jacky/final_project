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

import com.mjc.groupware.common.annotation.CheckPermission;
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
	
	@CheckPermission("SHARED_USER")
	@GetMapping("/shared/main/tree")
	@ResponseBody
	public List<Map<String, Object>> getFolderTree(@RequestParam("type") String type, Authentication auth) {
	    MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
	    Member member = memberDetails.getMember();

	    List<SharedFolder> folders;

	    switch (type) {
	        case "personal":
	            // ✅ 내 개인 폴더만 (내가 만든, folder_type = 1)
	            folders = folderRepository.findAll().stream()
	                .filter(f -> f.getFolderType() == 1
	                          && f.getMember().getMemberNo().equals(member.getMemberNo())
	                          && "N".equals(f.getFolderStatus()))
	                .toList();
	            break;
	        case "department":
	            // ✅ 모든 부서 폴더 (folder_type = 2)
	            folders = folderRepository.findAll().stream()
	                .filter(f -> f.getFolderType() == 2
	                          && "N".equals(f.getFolderStatus()))
	                .toList();
	            break;
	        case "public":
	            // ✅ 모든 공유 폴더 (folder_type = 3)
	            folders = folderRepository.findAll().stream()
	                .filter(f -> f.getFolderType() == 3
	                          && "N".equals(f.getFolderStatus()))
	                .toList();
	            break;
	        default:
	            throw new IllegalArgumentException("잘못된 type 값: " + type);
	    }

	    List<Map<String, Object>> result = new ArrayList<>();

	    for (SharedFolder folder : folders) {
	        Map<String, Object> node = new HashMap<>();
	        node.put("id", folder.getFolderNo());
	        node.put("parent", folder.getParentFolder() == null ? "#" : folder.getParentFolder().getFolderNo());
	        node.put("text", folder.getFolderName());
	        node.put("icon", "jstree-folder");
	        node.put("folder_type", folder.getFolderType()); // ✅ JS에서 사용할 수 있게
	        node.put("dept_no", folder.getDept() != null ? folder.getDept().getDeptNo() : null);
	        result.add(node);
	        
	        List<SharedFile> files = fileRepository.findByFolderFolderNo(folder.getFolderNo()).stream()
	            .filter(file -> "N".equals(file.getFileStatus()))
	            .toList();

	        for (SharedFile file : files) {
	            Map<String, Object> fileNode = new HashMap<>();
	            fileNode.put("id", "file-" + file.getFileNo());
	            fileNode.put("parent", folder.getFolderNo());
	            fileNode.put("text", file.getFileName());
	            fileNode.put("icon", "jstree-file");
	            fileNode.put("dept_no", folder.getDept() != null ? folder.getDept().getDeptNo() : null); // ✅ 추가
	            result.add(fileNode);
	        }
	    }

	    return result;
	}
	
	// 최상위,하위 폴더 생성.
	@CheckPermission("SHARED_USER")
	@PostMapping("/shared/folder/create")
	@ResponseBody
	public Map<String,String> createFolder(@RequestBody SharedFolderDto dto){
		sharedFolderService.createFolder(dto);
		return Map.of("message", "📁 폴더가 생성되었습니다.");
	}
	
	// 부모 폴더 타입 상속.
	@CheckPermission("SHARED_USER")
	@GetMapping("/shared/folder/type")
	@ResponseBody
	public Map<String, Integer> getFolderType(@RequestParam("folderId") Long folderId) {
	    SharedFolder folder = folderRepository.findById(folderId)
	        .orElseThrow(() -> new RuntimeException("상위 폴더를 찾을 수 없습니다."));
	    return Map.of("folderType", folder.getFolderType());
	}
	
	// 폴더 이동
	@CheckPermission("SHARED_USER")
	@PostMapping("/shared/folder/move")
	@ResponseBody
	public Map<String, String> moveFolder(@RequestBody Map<String, Long> payload) {
	    Long folderId = payload.get("folderId");
	    Long newParentId = payload.get("newParentId");
	    sharedFolderService.moveFolder(folderId, newParentId);
	    return Map.of("message", "폴더 위치가 변경되었습니다.");
	}
	
	@CheckPermission("SHARED_USER")
	@PostMapping("/shared/folder/delete")
	@ResponseBody
	public Map<String, Object> deleteFolder(@RequestBody Map<String, Long> payload, Authentication auth) {
	    Long folderId = payload.get("id");
	    Member member = ((MemberDetails) auth.getPrincipal()).getMember();

	    sharedFolderService.softDelete(folderId, member.getMemberNo());

	    return Map.of("message", "폴더가 삭제되었습니다.");
	}
	
	
}

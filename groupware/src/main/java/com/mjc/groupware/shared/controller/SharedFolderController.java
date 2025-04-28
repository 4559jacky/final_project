package com.mjc.groupware.shared.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.shared.dto.SharedFolderDto;
import com.mjc.groupware.shared.entity.SharedFolder;
import com.mjc.groupware.shared.repository.FolderRepository;
import com.mjc.groupware.shared.service.SharedFolderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SharedFolderController {
	
	private final FolderRepository folderRepository;
	private final SharedFolderService sharedFolderService;
	
	@GetMapping("/shared/main/tree")
	@ResponseBody
	public List<Map<String, Object>> getFolderTree(Authentication auth) {
		MemberDetails memberDetails = (MemberDetails) auth.getPrincipal(); // (1) 로그인한 사용자 정보 (MemberDetails)
	    Member member = memberDetails.getMember(); // 진짜 Member 꺼내기
	    
	    Long deptNo = (member.getDept() != null)? member.getDept().getDeptNo() : null;
	    
	    List<SharedFolder> folders;
	    
	    if(deptNo == null) {
	    	//  부서 없는 사용자 (예: 관리자) ➔ 공유 폴더만 가져오기
	    	folders = folderRepository.findSharedFolders();
	    }else {
	    	  // 일반 사용자 ➔ 내 개인 + 내 부서 + 공유 폴더
	    	folders = folderRepository.findByAccessControl(member.getMemberNo(), deptNo);
	    }

	    return folders.stream().map(folder -> {  // 가져온 폴더 목록을 jsTree용 형태로 변환.
	        Map<String, Object> node = new HashMap<>();
	        node.put("id", folder.getFolderNo()); // jsTree는 id, parent, text 3개 필요
	        node.put("parent", folder.getParentFolder() == null ? "#" : folder.getParentFolder().getFolderNo());
	        node.put("text", folder.getFolderName());
	        return node;
	    }).collect(Collectors.toList());
	}
	
	// 최상위,하위 폴더 생성.
	@PostMapping("/shared/folder/create")
	@ResponseBody
	public Map<String,String> createFolder(@RequestBody SharedFolderDto dto){
		sharedFolderService.createFolder(dto);
		return Map.of("message", "📁 폴더가 생성되었습니다.");
	}
	
	
}

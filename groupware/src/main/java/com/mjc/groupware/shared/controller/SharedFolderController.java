package com.mjc.groupware.shared.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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
	public List<Map<String, Object>> getFolderTree() {
	    List<SharedFolder> folders = folderRepository.findAll();

	    return folders.stream().map(folder -> {
	        Map<String, Object> node = new HashMap<>();
	        node.put("id", folder.getFolderNo());
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

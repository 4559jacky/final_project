package com.mjc.groupware.shared.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.shared.dto.SharedFolderDto;
import com.mjc.groupware.shared.entity.SharedFolder;
import com.mjc.groupware.shared.repository.FolderRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SharedFolderController {
	
	private final FolderRepository folderRepository;
	private final MemberRepository memberRepository;
	
	
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
	
	@PostMapping("/shared/folder/create")
	@ResponseBody
	public Map<String, String> createFolder(@RequestBody SharedFolderDto dto) {
	    Member member = memberRepository.findById(dto.getMember_no())
	        .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));

	    SharedFolder.SharedFolderBuilder builder = SharedFolder.builder()
	        .folderName(dto.getFolder_name())
	        .member(member);

	    if (dto.getFolder_parent_no() != null) {
	        builder.parentFolder(folderRepository.findById(dto.getFolder_parent_no()).orElse(null));
	    }

	    SharedFolder folder = builder.build();
	    folderRepository.save(folder);

	    return Map.of("message", "📁 폴더가 생성되었습니다.");
	}
	
}

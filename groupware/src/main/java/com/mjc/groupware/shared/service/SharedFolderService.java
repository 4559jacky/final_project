package com.mjc.groupware.shared.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mjc.groupware.shared.dto.SharedFileDto;
import com.mjc.groupware.shared.dto.SharedFolderDto;
import com.mjc.groupware.shared.entity.SharedFile;
import com.mjc.groupware.shared.entity.SharedFolder;
import com.mjc.groupware.shared.repository.FileRepository;
import com.mjc.groupware.shared.repository.FolderRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SharedFolderService {
	
	private final FolderRepository folderRepository;
	private final FileRepository fileRepository;
	
	// 폴더 생성.
	public void createFolder(SharedFolderDto dto) {
		 SharedFolder folder = dto.toEntity(); // member + parent까지 다 포함된 entity
		
		 folderRepository.save(folder);

	}

	//폴더랑 파일, 트리랑 테이블 동기화.
	public static List<Map<String, Object>> getFolderAndFileContents(Long folderNo) {
		
		List<Map<String, Object>> result = new ArrayList<>();

	    // 1. 하위 폴더
	    List<SharedFolder> folders = folderRepository.findByParentFolder_FolderNo(folderNo);
	    for (SharedFolder folder : folders) {
	        SharedFolderDto dto = new SharedFolderDto().toDto(folder);

	        Map<String, Object> map = new HashMap<>();
	        map.put("type", "folder");
	        map.put("no", dto.getFolder_no());
	        map.put("name", dto.getFolder_name());
	        map.put("regDate", dto.getReg_date());
	        map.put("shared", dto.getFolder_shared());
	        map.put("size", "-"); // 폴더는 사이즈 개념 없음
	        result.add(map);
	    }

	    // 2. 포함된 파일
	    List<SharedFile> files = fileRepository.findByFolder_FolderNo(folderNo);
	    for (SharedFile file : files) {
	        SharedFileDto dto = new SharedFileDto().toDto(file);

	        Map<String, Object> map = new HashMap<>();
	        map.put("type", "file");
	        map.put("no", dto.getFile_no());
	        map.put("name", dto.getFile_name());
	        map.put("regDate", dto.getReg_date());
	        map.put("shared", dto.getFile_shared());
	        map.put("size", formatFileSize(dto.getFile_size())); // KB, MB 포맷팅
	        result.add(map);
	    }

	    return result;
	}
	
	// 파일 사이즈
	private String formatFileSize(Long size) {
	    if (size == null) return "-";
	    if (size < 1024) return size + " B";
	    else if (size < 1024 * 1024) return (size / 1024) + " KB";
	    else return String.format("%.2f MB", size / 1024.0 / 1024.0);
	}
	
	
}



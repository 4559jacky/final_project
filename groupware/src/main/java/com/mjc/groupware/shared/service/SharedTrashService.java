package com.mjc.groupware.shared.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.shared.entity.SharedFile;
import com.mjc.groupware.shared.entity.SharedFolder;
import com.mjc.groupware.shared.repository.FileRepository;
import com.mjc.groupware.shared.repository.FolderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SharedTrashService {

    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;

    public Map<String, Object> loadTrashItems(String type, Member member) {
    	List<Map<String, Object>> result = new ArrayList<>();
    	
    	List<SharedFolder> folders = folderRepository.findByFolderStatus("Y");
        List<SharedFile> files = fileRepository.findByFileStatus("Y");

        // 🔥 분기 처리
        int typeValue = switch (type) {
            case "personal" -> 1;
            case "department" -> 2;
            case "public" -> 3;
            default -> throw new IllegalArgumentException("잘못된 타입");
        };

        // 🔍 폴더 필터링
        folders.stream()
            .filter(f -> f.getFolderType() == typeValue)
            .forEach(folder -> {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "folder");
                map.put("id", folder.getFolderNo());
                map.put("name", folder.getFolderName());
                map.put("size", null);
                map.put("deletedAt", folder.getFolderDeletedAt());
                result.add(map);
            });

        // 🔍 파일 필터링
        files.stream()
            .filter(file -> file.getFolder().getFolderType() == typeValue)
            .forEach(file -> {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "file");
                map.put("id", file.getFileNo());
                map.put("name", file.getFileName());
                map.put("size", file.getFileSize());
                map.put("deletedAt", file.getFileDeletedAt());
                result.add(map);
            });

        // 🔁 정렬 (삭제일 기준 내림차순)
        result.sort(Comparator.comparing(m -> ((LocalDateTime) m.get("deletedAt")), Comparator.reverseOrder()));

        return Map.of("items", result);
    }
    

@Transactional
public void restoreFolders(List<Long> folderIds) {
    if (folderIds == null) return;
    for (Long id : folderIds) {
        SharedFolder folder = folderRepository.findById(id).orElse(null);
        if (folder != null) {
            folder.setFolderStatus("N");
            folder.setFolderDeletedAt(null);
            folder.setFolderDeletedBy(null);
        }
    }
}

@Transactional
public void restoreFiles(List<Long> fileIds) {
    if (fileIds == null) return;
    for (Long id : fileIds) {
        SharedFile file = fileRepository.findById(id).orElse(null);
        if (file != null) {
            file.setFileStatus("N");
            file.setFileDeletedAt(null);
            file.setFileDeletedBy(null);
        }
    }
}

@Transactional
public void deleteFoldersPermanently(List<Long> folderIds) {
	 if (folderIds == null) return;
	    for (Long id : folderIds) {
	        SharedFolder folder = folderRepository.findById(id).orElse(null);
	        if (folder != null) {
	            folder.setFolderStatus("D");  // ✅ 실제 삭제 대신 상태값 변경
	        }
	    }
}

@Transactional
public void deleteFilesPermanently(List<Long> fileIds) {
    if (fileIds == null) return;
    for (Long id : fileIds) {
        SharedFile file = fileRepository.findById(id).orElse(null);
        if (file != null) {
            // 🔥 실제 파일 삭제 필요시 여기서 처리
        	 file.setFileStatus("D");
        }
    }
}
}

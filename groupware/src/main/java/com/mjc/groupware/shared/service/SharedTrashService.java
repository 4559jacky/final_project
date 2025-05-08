// SharedTrashService.java
package com.mjc.groupware.shared.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Map<String, Object> loadTrashItems() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 삭제된 폴더
        List<SharedFolder> deletedFolders = folderRepository.findByFolderStatus("Y");
        for (SharedFolder folder : deletedFolders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", folder.getFolderNo());
            map.put("type", "folder");
            map.put("name", folder.getFolderName());
            map.put("folderDeletedAt", folder.getFolderDeletedAt());
            result.add(map);
        }

        // 삭제된 파일
        List<SharedFile> deletedFiles = fileRepository.findByFileStatus("Y");
        for (SharedFile file : deletedFiles) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", file.getFileNo());
            map.put("type", "file");
            map.put("name", file.getFileName());
            map.put("size", file.getFileSize());
            map.put("deletedAt", file.getFileDeletedAt());
            result.add(map);
        }

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


package com.mjc.groupware.shared.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.shared.dto.SharedFileDto;
import com.mjc.groupware.shared.entity.SharedFile;
import com.mjc.groupware.shared.entity.SharedFolder;
import com.mjc.groupware.shared.repository.FileRepository;
import com.mjc.groupware.shared.repository.FolderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SharedFileService {
	
	private final FolderRepository folderRepository;
	private final FileRepository fileRepository;
	
	@Value("${ffupload.location}")
	private String filePath;
	
	public void saveFiles(List<MultipartFile> files, Long folderId) {
		SharedFolder folder = folderRepository.findById(folderId).orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));

		for (MultipartFile file : files) {
			try{
				String oriName = file.getOriginalFilename();
				String uuid = UUID.randomUUID().toString();
				String newName = uuid + "_" + oriName;
			
			Path savePath = Paths.get(filePath, newName);
			file.transferTo(savePath.toFile());
			
			SharedFileDto dto = SharedFileDto.builder()
					.file_name(oriName)
					.file_path(newName)
					.file_size(file.getSize())
					.file_status("N")
					.file_shared("N")
					.member_no(folder.getMember().getMemberNo()) //로그인 사용자
					.build();
			
			SharedFile entity = dto.toEntity();
			entity.setFolder(folder);
			
			fileRepository.save(entity);
			
			}catch(IOException e) {
				throw new RuntimeException("파일 업로드 실패: "+file.getOriginalFilename(), e);
			}
		}
	}
	
	
	// 파일/폴더 리스트 조회
	public List<Map<String, Object>> getFolderContent(Long folderId) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		List<SharedFolder> subFolders;
		
		if(folderId == null) {
			subFolders = folderRepository.findByParentFolderIsNull();
		}else {
			subFolders = folderRepository.findByParentFolderFolderNo(folderId);
		}
		
		for(SharedFolder folder : subFolders) {
			Map<String, Object> map = new HashMap<>();
			map.put("type", "folder");
			map.put("id", folder.getFolderNo());
			map.put("name", folder.getFolderName());
			map.put("regDate", folder.getRegDate());
			result.add(map);
		}
		
		// 파일은 폴더 선택했을 때만 표시
		if(folderId != null) {
			List<SharedFile> files = fileRepository.findByFolderFolderNo(folderId);
			for(SharedFile file : files) {
				Map<String, Object> map = new HashMap<>();
				map.put("type", "file");
				map.put("id", file.getFileNo());
				map.put("name", file.getFileName());
				map.put("size", file.getFileSize());
				map.put("regDate", file.getRegDate());
				result.add(map);
			}
		
		}
		
		return result;
	}
	
	// 파일 다운로드
	public ResponseEntity<Resource> downloadFile(Long fileId) {
		SharedFile fileEntity = fileRepository.findById(fileId)
				.orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
		
		String fullPath = filePath + "/" + fileEntity.getFilePath(); // 실제 저장된 파일 경로
		FileSystemResource resource = new FileSystemResource(fullPath);
		
		if(!resource.exists()) {
			throw new RuntimeException("파일이 존재하지 않습니다:"+fullPath);
		}
		
		// 한글 파일명 처리(UTF-8 인코딩)
		String encodedName = URLEncoder.encode(fileEntity.getFileName(), StandardCharsets.UTF_8);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName +"\"");
		
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}


	
}


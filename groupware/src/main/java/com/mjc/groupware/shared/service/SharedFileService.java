package com.mjc.groupware.shared.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.member.entity.Member;
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
	
	public Map<String, Object> getFolderContent(Long folderId, List<Long> folderIds, Member member, String type) {
	    // ✅ folderId가 null이고 folderIds만 있는 경우
	    if (folderId == null && folderIds != null && !folderIds.isEmpty()) {
	        List<Map<String, Object>> list = new ArrayList<>();

	        List<SharedFolder> folders = folderRepository.findAllById(folderIds).stream()
	                .filter(f -> "N".equals(f.getFolderStatus()))
	                .sorted(Comparator.comparing(SharedFolder::getFolderName))
	                .toList();

	        for (SharedFolder folder : folders) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("type", "folder");
	            map.put("id", folder.getFolderNo());
	            map.put("name", folder.getFolderName());
	            map.put("regDate", folder.getRegDate());
	            list.add(map);
	        }

	        Map<String, Object> result = new HashMap<>();
	        result.put("items", list);
	        result.put("parentFolderNo", null);
	        return result;
	    }

	    // ✅ 그렇지 않으면 기존 로직으로 위임
	    return getFolderContent(folderId, null, member, type);
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


	public void moveFile(Long fileId, Long newFolderId) {
	    SharedFile file = fileRepository.findById(fileId)
	        .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

	    SharedFolder folder = folderRepository.findById(newFolderId)
	        .orElseThrow(() -> new RuntimeException("대상 폴더를 찾을 수 없습니다."));

	    file.setFolder(folder);
	    fileRepository.save(file);
	}
	
	public void softDelete(Long fileId, Long memberNo) {
	    SharedFile file = fileRepository.findById(fileId)
	            .orElseThrow(() -> new RuntimeException("파일 없음"));

	    file.setFileStatus("Y");
	    file.setFileDeletedBy(memberNo);
	    file.setFileDeletedAt(LocalDateTime.now());

	    fileRepository.save(file);
	}
	

}


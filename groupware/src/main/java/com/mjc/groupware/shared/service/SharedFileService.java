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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
	
	public void saveFiles(List<MultipartFile> files, Long folderId,Member member) {
		SharedFolder folder = folderRepository.findById(folderId).orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));
		
		Long uploaderNo = folder.getMember() != null ? folder.getMember().getMemberNo() : /* fallback */  member.getMemberNo();
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
				    .member_no(uploaderNo)
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
	    Map<String, Object> result = new HashMap<>();
	    List<Map<String, Object>> list = new ArrayList<>();

	    List<SharedFolder> subFolders;
	    SharedFolder currentFolder = null;


	    // folderId도 없고 folderIds도 비어 있음 → 최상위에 아무것도 없음
	    if (folderId == null && (folderIds == null || folderIds.isEmpty())) {
	        result.put("items", new ArrayList<>());
	        result.put("parentFolderNo", null);
	        return result;
	    }
	    
	 // ✅ folderId가 null이고 folderIds가 들어왔을 경우 루트 폴더 목록 조회
	    if (folderId == null && folderIds != null && !folderIds.isEmpty()) {
	        subFolders = folderRepository.findAllById(folderIds).stream()
	            .filter(f -> "N".equals(f.getFolderStatus()))
	            .sorted(Comparator.comparing(SharedFolder::getFolderName))
	            .toList();

	        for (SharedFolder folder : subFolders) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("type", "folder");
	            map.put("id", folder.getFolderNo());
	            map.put("name", folder.getFolderName());
	            map.put("regDate", folder.getRegDate());
	            map.put("deptNo", folder.getDept() != null ? folder.getDept().getDeptNo() : null); // ✅ 추가
	            list.add(map);
	        }

	        result.put("items", list);
	        result.put("parentFolderNo", null);
	        return result;
	    }

	    // ✅ 폴더 클릭했을 경우
	    if (folderId != null) {
	        currentFolder = folderRepository.findById(folderId)
	                .filter(f -> "N".equals(f.getFolderStatus()))
	                .orElseThrow(() -> new RuntimeException("해당 폴더가 없거나 삭제된 상태입니다."));

	        boolean isOwner = currentFolder.getFolderType() == 1 &&
	                currentFolder.getMember() != null &&
	                member != null &&
	                currentFolder.getMember().getMemberNo().equals(member.getMemberNo());

	        boolean sameDept = currentFolder.getFolderType() == 2 &&
	                currentFolder.getDept() != null &&
	                member != null &&
	                member.getDept() != null &&
	                currentFolder.getDept().getDeptNo().equals(member.getDept().getDeptNo());

	        boolean isShared = currentFolder.getFolderType() == 3;

	        if (!(isOwner || sameDept || isShared)) {
	            // 여기가 핵심: member가 null이거나 조건에 안 맞으면 접근권한 실패
	            throw new RuntimeException("해당 폴더에 접근 권한이 없습니다.");
	        }
	        	
	        subFolders = folderRepository.findByParentFolderFolderNo(folderId).stream()
	            .filter(folder -> {
	                if (folder.getParentFolder() == null ||
	                    !folder.getParentFolder().getFolderNo().equals(folderId)) {
	                    return false; // 🔥 루프 방지 조건
	                }

	                boolean childIsOwner = folder.getFolderType() == 1 &&
	                        folder.getMember() != null &&
	                        member != null &&
	                        folder.getMember().getMemberNo().equals(member.getMemberNo());

	                    boolean childSameDept = folder.getFolderType() == 2 &&
	                        folder.getDept() != null &&
	                        member.getDept() != null &&
	                        folder.getDept().getDeptNo().equals(member.getDept().getDeptNo());

	                    boolean childIsShared = folder.getFolderType() == 3;

	                    return childIsOwner || childSameDept || childIsShared;
	            })
	            .sorted(Comparator.comparing(SharedFolder::getFolderName))
	            .toList();

	        for (SharedFolder folder : subFolders) {
	            if (!"N".equals(folder.getFolderStatus())) continue;
	            Map<String, Object> map = new HashMap<>();
	            map.put("type", "folder");
	            map.put("id", folder.getFolderNo());
	            map.put("name", folder.getFolderName());
	            map.put("regDate", folder.getRegDate());
	            map.put("deptNo", folder.getDept() != null ? folder.getDept().getDeptNo() : null); // ✅ 추가
	            list.add(map);
	        }

	        // ✅ 해당 폴더 내 파일 추가
	        List<SharedFile> files = fileRepository.findByFolderFolderNo(folderId).stream()
	            .filter(f -> "N".equals(f.getFileStatus()))
	            .sorted(Comparator.comparing(SharedFile::getFileName))
	            .toList();

	        for (SharedFile file : files) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("type", "file");
	            map.put("id", file.getFileNo());
	            map.put("name", file.getFileName());
	            map.put("size", file.getFileSize());
	            map.put("regDate", file.getRegDate());
	            list.add(map);
	        }

	        result.put("items", list);
	        result.put("parentFolderNo",
	                currentFolder.getParentFolder() != null
	                        ? currentFolder.getParentFolder().getFolderNo()
	                        : null);
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


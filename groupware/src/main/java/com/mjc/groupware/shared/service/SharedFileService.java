package com.mjc.groupware.shared.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	public Map<String, Object> getFolderContent(Long folderId, Member member) {
	    Map<String, Object> result = new HashMap<>();
	    List<Map<String, Object>> list = new ArrayList<>();
	    List<SharedFolder> subFolders;
	    SharedFolder currentFolder = null;
	    if (folderId == null) {
	        // 로그인 사용자 기준으로 내가 볼 수 있는 최상위 폴더만 필터링
	        subFolders = folderRepository.findByParentFolderIsNull().stream()
	                .filter(folder -> {
	                    boolean isOwner = folder.getFolderType() == 1 && folder.getMember() != null &&
	                            folder.getMember().getMemberNo().equals(member.getMemberNo());

	                    boolean sameDept = folder.getFolderType() == 2 &&
	                            folder.getDept() != null &&
	                            member.getDept() != null &&
	                            folder.getDept().getDeptNo().equals(member.getDept().getDeptNo());

	                    boolean isShared = folder.getFolderType() == 3;

	                    return isOwner || sameDept || isShared;
	                })
	                .collect(Collectors.toList());	    
	    } else {
	        // 🔐 현재 폴더 접근 확인
	        currentFolder = folderRepository.findById(folderId)
	                .orElseThrow(() -> new RuntimeException("해당 폴더가 없습니다."));

	        boolean isOwner = currentFolder.getMember() != null &&
	                currentFolder.getMember().getMemberNo().equals(member.getMemberNo());
	        boolean sameDept = currentFolder.getDept() != null &&
	                member.getDept() != null &&
	                currentFolder.getDept().getDeptNo().equals(member.getDept().getDeptNo());
	        boolean isShared = currentFolder.getFolderType() == 3;

	        if (!(isOwner || sameDept || isShared)) {
	            throw new RuntimeException("해당 폴더에 접근 권한이 없습니다.");
	        }

	        // ✅ 자식 폴더도 사용자 기준으로 필터링
	        subFolders = folderRepository.findByParentFolderFolderNo(folderId).stream()
	                .filter(folder -> {
	                    boolean childIsOwner = folder.getMember() != null &&
	                            folder.getMember().getMemberNo().equals(member.getMemberNo());
	                    boolean childSameDept = folder.getDept() != null &&
	                            member.getDept() != null &&
	                            folder.getDept().getDeptNo().equals(member.getDept().getDeptNo());
	                    boolean childIsShared = folder.getFolderType() == 3;
	                    return childIsOwner || childSameDept || childIsShared;
	                })
	                .collect(Collectors.toList());
	    }

	    // 📁 폴더 매핑
	    subFolders.sort(Comparator.comparing(SharedFolder::getFolderName));
	    for (SharedFolder folder : subFolders) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("type", "folder");
	        map.put("id", folder.getFolderNo());
	        map.put("name", folder.getFolderName());
	        map.put("regDate", folder.getRegDate());
	        list.add(map);
	    }

	    // 📄 파일 매핑 (접근 권한 있는 폴더에서만)
	    if (folderId != null && currentFolder != null) {
	        boolean isOwner = currentFolder.getMember() != null &&
	                currentFolder.getMember().getMemberNo().equals(member.getMemberNo());
	        boolean sameDept = currentFolder.getDept() != null &&
	                member.getDept() != null &&
	                currentFolder.getDept().getDeptNo().equals(member.getDept().getDeptNo());
	        boolean isShared = currentFolder.getFolderType() == 3;

	        if (isOwner || sameDept || isShared) {
	            List<SharedFile> files = fileRepository.findByFolderFolderNo(folderId);
	            files.sort(Comparator.comparing(SharedFile::getFileName));

	            for (SharedFile file : files) {
	                Map<String, Object> map = new HashMap<>();
	                map.put("type", "file");
	                map.put("id", file.getFileNo());
	                map.put("name", file.getFileName());
	                map.put("size", file.getFileSize());
	                map.put("regDate", file.getRegDate());
	                list.add(map);
	            }
	        }
	    }

	    result.put("items", list);
	    result.put("parentFolderNo",
	            currentFolder != null && currentFolder.getParentFolder() != null
	                    ? currentFolder.getParentFolder().getFolderNo()
	                    : null);

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


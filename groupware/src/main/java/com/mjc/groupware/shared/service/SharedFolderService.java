package com.mjc.groupware.shared.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
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
	private final MemberRepository memberRepository;
	private final FileRepository fileRepository;
	
	// 폴더 생성.
	public void createFolder(SharedFolderDto dto) {
		 SharedFolder folder = dto.toEntity(); // member + parent까지 다 포함된 entity
		

		 folder.setFolderStatus("N");
		 
		 Member member = memberRepository.findById(dto.getMember_no())
				 .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
		 
		 folder.setMember(member);
		 
		 if(member.getDept() != null) {
			 folder.setDept(member.getDept());
		 }
		 if(member.getPos() != null) {
			 folder.setPos(member.getPos());
		 }

		 folderRepository.save(folder);
	}
	
	public void moveFolder(Long folderId, Long newParentId) {
		System.out.println("➡ 폴더 이동: " + folderId + " → " + newParentId); // 확인용
	    SharedFolder folder = folderRepository.findById(folderId)
	        .orElseThrow(() -> new RuntimeException("이동 대상 폴더 없음"));

	    SharedFolder newParent = (newParentId == null) ? null :
	        folderRepository.findById(newParentId).orElse(null);

	    folder.setParentFolder(newParent);
	    folderRepository.save(folder);
	}
	
	@Transactional
	public void softDelete(Long folderId, Long memberNo) {
	    SharedFolder folder = folderRepository.findById(folderId)
	            .orElseThrow(() -> new RuntimeException("폴더 없음"));

	    markFolderDeleted(folder, memberNo);
	}

	private void markFolderDeleted(SharedFolder folder, Long memberNo) {
	    folder.setFolderStatus("Y");
	    folder.setFolderDeletedBy(memberNo);
	    folder.setFolderDeletedAt(LocalDateTime.now());
	    folderRepository.save(folder);

	    // 하위 폴더도 재귀적으로 삭제
	    List<SharedFolder> childFolders = folderRepository.findByParentFolderFolderNo(folder.getFolderNo());
	    for (SharedFolder child : childFolders) {
	        markFolderDeleted(child, memberNo);
	    }

	    // 하위 파일도 soft-delete
	    List<SharedFile> files = fileRepository.findByFolderFolderNo(folder.getFolderNo());
	    for (SharedFile file : files) {
	        file.setFileStatus("Y");
	        file.setFileDeletedBy(memberNo);
	        file.setFileDeletedAt(LocalDateTime.now());
	        fileRepository.save(file);
	    }
	}
	
	public List<SharedFolderDto> getDeletedFolders() {
	    List<SharedFolder> deletedFolders = folderRepository.findByFolderStatus("Y");

	    return deletedFolders.stream()
	        .map(folder -> SharedFolderDto.builder()
	            .folder_no(folder.getFolderNo())
	            .folder_name(folder.getFolderName())
	            .folder_deleted_by(folder.getFolderDeletedBy())
	            .folder_deleted_at(folder.getFolderDeletedAt())
	            .build())
	        .collect(Collectors.toList());
	}
	
	
	
}



package com.mjc.groupware.shared.service;



import org.springframework.stereotype.Service;


import com.mjc.groupware.shared.dto.SharedFolderDto;
import com.mjc.groupware.shared.entity.SharedFolder;
import com.mjc.groupware.shared.repository.FileRepository;
import com.mjc.groupware.shared.repository.FolderRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SharedFolderService {
	
	private final FolderRepository folderRepository;
	
	// 폴더 생성.
	public void createFolder(SharedFolderDto dto) {
		 SharedFolder folder = dto.toEntity(); // member + parent까지 다 포함된 entity
		
		 folderRepository.save(folder);

	}
}



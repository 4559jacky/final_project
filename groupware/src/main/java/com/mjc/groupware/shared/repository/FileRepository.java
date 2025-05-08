package com.mjc.groupware.shared.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.shared.entity.SharedFile;
import com.mjc.groupware.shared.entity.SharedFolder;

public interface FileRepository extends JpaRepository<SharedFile, Long>, JpaSpecificationExecutor<SharedFile>{
	
	List<SharedFile> findByFolderFolderNo(Long folderNo);
	
	
	List<SharedFile> findByFolderAndFileStatus(SharedFolder folder, String fileStatus);

	List<SharedFile> findByFileStatus(String status);
	
	List<SharedFile> findByFolderFolderNoAndFileStatus(Long folderNo, String fileStatus);
}

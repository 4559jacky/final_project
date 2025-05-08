package com.mjc.groupware.shared.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.shared.entity.SharedFile;
import com.mjc.groupware.shared.entity.SharedFolder;

public interface FileRepository extends JpaRepository<SharedFile, Long>, JpaSpecificationExecutor<SharedFile>{
	
	List<SharedFile> findByFolderFolderNo(Long folderNo);
	
	
	List<SharedFile> findByFolderAndFileStatus(SharedFolder folder, String fileStatus);

	List<SharedFile> findByFileStatus(String status);
	
	List<SharedFile> findByFolderFolderNoAndFileStatus(Long folderNo, String fileStatus);


    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM SharedFile f WHERE f.fileStatus = :status")
    long sumFileSizeByStatus(@Param("status") String status);

    List<SharedFile> findByFileStatusAndMemberMemberNo(String status, Long memberNo);
    List<SharedFile> findByFileStatusAndFolderFolderType(String status, int type);

}

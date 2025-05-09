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

	//개인용
	@Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM SharedFile f WHERE f.member.memberNo = :memberNo AND f.fileStatus = :status")
	long sumSizeByOwnerAndStatus(@Param("memberNo") Long memberNo, @Param("status") String status);
	// 부서용
	@Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM SharedFile f WHERE f.folder.dept.deptNo = :deptNo AND f.folder.folderType = 2 AND f.fileStatus = :status")
	long sumSizeByDeptAndStatus(@Param("deptNo") Long deptNo, @Param("status") String status);
	// 공용용
	@Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM SharedFile f WHERE f.folder.folderType = 3 AND f.fileStatus = :status")
	long sumSizeByTypeAndStatus(@Param("status") String status);
}

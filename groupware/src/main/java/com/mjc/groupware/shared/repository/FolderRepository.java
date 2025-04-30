package com.mjc.groupware.shared.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.shared.entity.SharedFolder;

public interface FolderRepository extends JpaRepository<SharedFolder, Long>, JpaSpecificationExecutor<SharedFolder> {

    // 특정 폴더의 하위 폴더 찾기
    List<SharedFolder> findByParentFolderFolderNo(Long parentFolderId);
    List<SharedFolder> findByParentFolderIsNull();
    
    
    // 이름순으로 정렬.
    @Query("SELECT f FROM SharedFolder f WHERE (f.folderType = 1 AND f.member.memberNo = :memberNo) OR (f.folderType = 2 AND f.dept.deptNo = :deptNo) OR (f.folderType = 3) ORDER BY f.folderName ASC")
    List<SharedFolder> findByAccessControl(@Param("memberNo") Long memberNo, @Param("deptNo") Long deptNo);

    @Query("SELECT f FROM SharedFolder f WHERE f.folderType = 3 ORDER BY f.folderName ASC")
    List<SharedFolder> findSharedFolders();

}

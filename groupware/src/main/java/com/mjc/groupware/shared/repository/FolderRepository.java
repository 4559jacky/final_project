package com.mjc.groupware.shared.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.shared.entity.SharedFolder;

public interface FolderRepository extends JpaRepository<SharedFolder, Long>, JpaSpecificationExecutor<SharedFolder> {

    // 특정 폴더의 하위 폴더 찾기
    List<SharedFolder> findByParentFolder_FolderNo(Long folderNo);

    // 로그인한 사용자가 볼 수 있는 폴더 트리 가져오기 (개인/부서/공용) + 정렬
    @Query("SELECT f FROM SharedFolder f WHERE (f.folderType = 1 AND f.member.memberNo = :memberNo) OR (f.folderType = 2 AND f.dept.deptNo = :deptNo) OR (f.folderType = 3) ORDER BY CASE WHEN f.folderType = 1 THEN 1 WHEN f.folderType = 2 THEN 2 WHEN f.folderType = 3 THEN 3 ELSE 4 END, f.folderNo ASC")
    List<SharedFolder> findByAccessControl(@Param("memberNo") Long memberNo, @Param("deptNo") Long deptNo);


    // 부서가 없는 사용자용 (공용 폴더만 조회)
    @Query("SELECT f FROM SharedFolder f WHERE f.folderType = 3ORDER BY f.folderNo ASC")
    List<SharedFolder> findSharedFolders();
}

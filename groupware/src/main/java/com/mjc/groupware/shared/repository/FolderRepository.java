package com.mjc.groupware.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.shared.entity.SharedFolder;

public interface FolderRepository extends JpaRepository<SharedFolder, Long>,JpaSpecificationExecutor<SharedFolder> {

}

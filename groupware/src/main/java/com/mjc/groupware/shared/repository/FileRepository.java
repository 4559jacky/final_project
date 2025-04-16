package com.mjc.groupware.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.shared.entity.SharedFile;

public interface FileRepository extends JpaRepository<SharedFile, Long>, JpaSpecificationExecutor<SharedFile>{

}

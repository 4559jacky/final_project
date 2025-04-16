package com.mjc.groupware.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.shared.entity.SharedWith;

public interface WithRepository extends JpaRepository<SharedWith, Long>,JpaSpecificationExecutor<SharedWith>{

}

package com.mjc.groupware.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.shared.entity.Shared;

public interface SharedRepository extends JpaRepository<Shared, Long>,JpaSpecificationExecutor<Shared> {

	

}

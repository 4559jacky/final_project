package com.mjc.groupware.shared.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.shared.entity.Shared;

public interface SharedRepository extends JpaRepository<Shared, Long>,JpaSpecificationExecutor<Shared> {

	Page<Shared> findAll(Specification<Shared> spec, Pageable pageble);
	
	List<Shared> findBySharedTitleContaining(String keyword);
	List<Shared> findBySharedContentContaining(String keyword);
	List<Shared> findBySharedTitleContainingIgnoreCaseOrSharedContentContainingIgnoreCase(String title, String content, Sort sort);
}

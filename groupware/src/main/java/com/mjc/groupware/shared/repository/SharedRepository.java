package com.mjc.groupware.shared.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mjc.groupware.shared.entity.Shared;

public interface SharedRepository extends JpaRepository<Shared, Long> {
	
	// 3. Sprcification 사용
		Page<Shared> findAll(Specification<Shared> spec, Pageable pageble);
		
		// 1. 메소드 네이밍
		List<Shared> findBySharedTitleContaining(String keyword);
		List<Shared> findBySharedContentContaining(String keyword);
		List<Shared> findBySharedTitleContainingOrSharedContentContaining(String contentKeyword,String titleKeyword);

		// 2. JPQL 이용
		@Query(value="SELECT s FROM Shared s WHERE s.sharedTitle LIKE CONCAT('%',?1,'%')")
		List<Shared> findByTitleLike(String keyword);
		
		@Query(value="SELECT s FROM Shared s WHERE s.sharedContent LIKE CONCAT('%',?1,'%')")
		List<Shared> findByContentLike(String keyword);
		
		@Query(value="SELECT s FROM Shared b WHERE s.sharedTitle LIKE CONCAT('%',:title,'%') OR b.boardContent LIKE CONCAT('%',:content,'%')")
		List<Shared> findByContentLikeOrfindByTitleLike(String title, String content);
	}


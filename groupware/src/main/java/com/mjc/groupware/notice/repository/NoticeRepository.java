package com.mjc.groupware.notice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long>,JpaSpecificationExecutor<Notice> {

	Page<Notice> findAll(Specification<Notice> spec, Pageable pageble);
	
	List<Notice> findByNoticeTitleContaining(String keyword);
	List<Notice> findByNoticeContentContaining(String keyword);
	List<Notice> findByNoticeTitleContainingIgnoreCaseOrNoticeContentContainingIgnoreCase(String title, String content, Sort sort);
}

package com.mjc.groupware.notice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long>,JpaSpecificationExecutor<Notice> {

	Page<Notice> findAll(Specification<Notice> spec, Pageable pageble);
	// 일반글 목록 (페이징)
	Page<Notice> findByNoticeFix(String fix, Pageable pageable);
	
	List<Notice> findByNoticeTitleContaining(String keyword);
	List<Notice> findByNoticeContentContaining(String keyword);
	List<Notice> findByNoticeTitleContainingIgnoreCaseOrNoticeContentContainingIgnoreCase(String title, String content, Sort sort);
	
	// 고정글 목록
	List<Notice> findByNoticeFixOrderByNoticeNoDesc(String fix);

	@Modifying
	@Query("UPDATE Notice n SET n.views = n.views + 1 WHERE n.noticeNo = :noticeNo")
	void increaseViews(@Param("noticeNo") Long noticeNo);
	
	List<Notice> findByNoticeFixAndNoticeStatusOrderByNoticeNoDesc(String string, String string2);
}

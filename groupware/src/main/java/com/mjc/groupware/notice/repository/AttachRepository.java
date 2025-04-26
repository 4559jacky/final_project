package com.mjc.groupware.notice.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.board.entity.BoardAttach;
import com.mjc.groupware.notice.entity.Attach;
import com.mjc.groupware.notice.entity.Notice;

public interface AttachRepository extends JpaRepository<Attach, Long>{

	List<Attach> findByNotice(Notice notice);
}

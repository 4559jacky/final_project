package com.mjc.groupware.notice.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.notice.dto.NoticeDto;
import com.mjc.groupware.notice.entity.Notice;
import com.mjc.groupware.notice.repository.NoticeRepository;
import com.mjc.groupware.notice.specification.NoticeSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository repository;
    private final MemberRepository memberRepository; // 추가
    
    //게시글 생성
    public int createNoticeApi(NoticeDto dto) {
        // memberNo로 Member 객체 조회
        Member member = memberRepository.findById(dto.getMember_no())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + dto.getMember_no()));

        // Shared 엔티티 생성
        Notice entity = dto.toEntity();
        entity = Notice.builder()
                .noticeTitle(entity.getNoticeTitle())
                .noticeContent(entity.getNoticeContent())
                .views(entity.getViews() != 0 ? entity.getViews() : 0)
                .member(member) // 조회한 Member 객체 설정
                .build();

        Notice saved = repository.save(entity);
        return saved != null ? 1 : 0;
    }

    // 게시글 목록 조회 + 게시글 검색 기능 추가 + 정렬 기능 + 페이징
    public Page<Notice> searchNotice(Integer searchType, String keyword, String sort, int page) {
        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    	Sort sortObj = Sort.by(direction, "regDate");
    	Pageable pageable = PageRequest.of(page, 10, sortObj);
    	if (keyword == null || keyword.isBlank()) {
            return repository.findAll(pageable);
        } 
    	  Specification<Notice> spec = null;

    	    if (searchType == null || searchType == 1) { // 제목
    	        spec = NoticeSpecification.noticeTitleContains(keyword);
    	    } else if (searchType == 2) { // 내용
    	        spec = NoticeSpecification.noticeContentContains(keyword);
    	    } else if (searchType == 3) { // 제목+내용
    	        spec = Specification.where(NoticeSpecification.noticeTitleContains(keyword))
    	                            .or(NoticeSpecification.noticeContentContains(keyword));
    	    }

    	    return repository.findAll(spec, pageable);
    }
    
    // 게시글 상세 조회 (조회수 증가 포함)
    public Notice getNoticeDetail(Long noticeNo) {
        Notice notice = repository.findById(noticeNo).orElse(null);
        if (notice != null) {
            // 조회수 증가
        	notice = Notice.builder()
                    .noticeNo(notice.getNoticeNo())
                    .noticeTitle(notice.getNoticeTitle())
                    .noticeContent(notice.getNoticeContent())
                    .views(notice.getViews() + 1) // 조회수 1 증가
                    .member(notice.getMember())
                    .regDate(notice.getRegDate())
                    .modDate(notice.getModDate())
                    .build();
            repository.save(notice); // 업데이트된 조회수 저장
        }
        return notice;
    }
    
    // 게시글 수정 1
    public Notice getNoticeUpdate(Long noticeNo) {
    	Notice notice = repository.findById(noticeNo).orElse(null);
    	return notice;
    }
    
    
    // 게시글 수정 2
	public int updateNotice(NoticeDto dto) {
		Notice notice = repository.findById(dto.getNotice_no()).orElse(null);
		notice.update(dto.getNotice_title(), dto.getNotice_content(), LocalDateTime.now());
		repository.save(notice);
		return 1;
	}
	
	// 게시글 삭제
	// 완전히 삭제 되는게 아니라, 삭제 여부에 따라 화면 딴에서는 보이고 안보이고 구별됨. ex N,Y
	// db에는 저장이 되있는데, 여부에따라 화면에 보이고 안보이고의 차이일뿐.
	// delete가 아니라 update로 해야함.
	public void deleteNotice(Long noticeNo) {
		repository.deleteById(noticeNo);
	}

	


}
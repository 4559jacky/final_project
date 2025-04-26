package com.mjc.groupware.notice.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.notice.dto.NoticeDto;
import com.mjc.groupware.notice.entity.Attach;
import com.mjc.groupware.notice.entity.Notice;
import com.mjc.groupware.notice.repository.AttachRepository;
import com.mjc.groupware.notice.repository.NoticeRepository;
import com.mjc.groupware.notice.specification.NoticeSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository repository;
    private final MemberRepository memberRepository; // 추가
    private final AttachRepository attachRepository;
    private final AttachService attachService;
    
    //게시글 생성
    public int createNoticeApi(NoticeDto dto, List<MultipartFile> files) {
        // memberNo로 Member 객체 조회
        Member member = memberRepository.findById(dto.getMember_no())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + dto.getMember_no()));

        Notice entity = Notice.builder()
                .noticeTitle(dto.getNotice_title())
                .noticeContent(dto.getNotice_content())
                .noticeFix(dto.getNotice_fix() != null ? dto.getNotice_fix() : "N")
                .noticeEmergency(dto.getNotice_emergency() != null ? dto.getNotice_emergency() : "N") // ✅ 추가
                .noticeStatus("N")
                .views(0)
                .member(member)
                .build();

        Notice saved = repository.save(entity);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        attachService.saveFile(file, saved);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            }
        }
        return 1;
    }

 // 게시글 목록 조회 + 게시글 검색 기능 추가 + 정렬 기능 + 페이징
    public Page<Notice> searchNotice(Integer searchType, String keyword, String sort, int page) {
        Sort sortObj;

        // 정렬 조건 분기: views, asc, desc
        if ("asc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Order.desc("noticeFix"), Sort.Order.asc("regDate")); // 오래된순
        } else if ("views".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Order.desc("noticeFix"), Sort.Order.desc("views"));  // 조회순
        } else {
            sortObj = Sort.by(Sort.Order.desc("noticeFix"), Sort.Order.desc("regDate")); // 최신순
        }

        Pageable pageable = PageRequest.of(page, 10, sortObj);

        // 🔍 검색어가 없으면 전체 반환 (정렬만 적용됨)
        if (keyword == null || keyword.isBlank()) {
        	Specification<Notice> spec = NoticeSpecification.isNotFixed().and((root, query, cb) -> cb.equal(root.get("noticeStatus"), "N"));  // ⬅ 고정글 제외
            return repository.findAll(spec, pageable);
        } 

        Specification<Notice> spec = NoticeSpecification.isNotFixed();

        if (searchType == null || searchType == 1) { // 제목
            spec = spec.and(NoticeSpecification.noticeTitleContains(keyword));
        } else if (searchType == 2) { // 내용
            spec = spec.and(NoticeSpecification.noticeContentContains(keyword));
        } else if (searchType == 3) { // 제목+내용
            spec = spec.and(
                Specification.where(NoticeSpecification.noticeTitleContains(keyword))
                             .or(NoticeSpecification.noticeContentContains(keyword))
            );
        }

        return repository.findAll(spec, pageable);
    }
    
    // 게시글 상세 조회 (조회수 증가 포함)
    @Transactional
    public Notice getNoticeDetail(Long noticeNo) {
        repository.increaseViews(noticeNo); // ✅ DB에서 직접 조회수 +1
        return repository.findById(noticeNo).orElse(null); // ✅ 최신값 다시 조회
    }
    
    // 게시글 수정 1
    public NoticeDto getNoticeUpdate(Long noticeNo) {
    	Notice notice = repository.findById(noticeNo).orElse(null);
    	if (notice == null) return null;
    	List<Attach> attachList = attachRepository.findByNotice(notice);

        NoticeDto dto = NoticeDto.builder()
            .notice_no(notice.getNoticeNo())
            .notice_title(notice.getNoticeTitle())
            .notice_content(notice.getNoticeContent())
            .member_no(notice.getMember().getMemberNo())
            .attachList(attachList) // 여기
            .notice_fix(notice.getNoticeFix())
            .notice_emergency(notice.getNoticeEmergency())
            .build();

    	return dto;
    }
    
    
    // 게시글 수정 2
	public int updateNotice(NoticeDto dto, List<MultipartFile> files, List<Long> deleteFiles) {
		Notice notice = repository.findById(dto.getNotice_no()).orElse(null);
		if(notice == null) return 0;
		
		boolean isContentChanged =
			 !notice.getNoticeTitle().equals(dto.getNotice_title()) ||
			 !notice.getNoticeContent().equals(dto.getNotice_content());
		
		boolean isFileChanged = 
			     (files != null && !files.isEmpty()) || 
			     (deleteFiles != null && !deleteFiles.isEmpty());
		 
		boolean shouldUpdateModDate = isContentChanged || isFileChanged; 
		 
		if (shouldUpdateModDate) {
	        notice.update(dto.getNotice_title(), dto.getNotice_content(), LocalDateTime.now());
	    } else {
	        notice.update(dto.getNotice_title(), dto.getNotice_content(), notice.getModDate());
	    }
		
		notice.setNoticeFix(dto.getNotice_fix() != null ? dto.getNotice_fix() : "N");
		notice.setNoticeEmergency(dto.getNotice_emergency() != null ? dto.getNotice_emergency() : "N");
		
		repository.save(notice);

	    if (deleteFiles != null) {
	        for (Long id : deleteFiles) {
	            try {
	                attachService.deleteAttachById(id);  // 실제 삭제
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
		
		if (files != null && !files.isEmpty()) {
	        for (MultipartFile file : files) {
	            if (!file.isEmpty()) {
	                try {
	                    attachService.saveFile(file, notice);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    return 0;
	                }
	            }
	        }
	    }
		
		return 1;
	}
	
	// 게시글 삭제
	// 완전히 삭제 되는게 아니라, 삭제 여부에 따라 화면 딴에서는 보이고 안보이고 구별됨. ex N,Y
	// db에는 저장이 되있는데, 여부에따라 화면에 보이고 안보이고의 차이일뿐.
	// delete가 아니라 update로 해야함.
	public void deleteNotice(Long noticeNo) {
	    Notice notice = repository.findById(noticeNo).orElse(null);
	    if (notice != null) {
	        notice.setNoticeStatus("Y");  // ✅ 삭제 상태로 변경
	        repository.save(notice);
	    }
	}
	
	// 일반글, 고정글 따로 가져옴.
	public List<Notice> getFixedNotices() {
		return repository.findByNoticeFixAndNoticeStatusOrderByNoticeNoDesc("Y", "N"); // ✅ 조건 추가
	}

	public Page<Notice> getNormalNotices(Pageable pageable) {
	    return repository.findByNoticeFix("N", pageable);
	}


}
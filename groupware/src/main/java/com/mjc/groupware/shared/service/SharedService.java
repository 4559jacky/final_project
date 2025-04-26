package com.mjc.groupware.shared.service;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.shared.dto.SharedDto;
import com.mjc.groupware.shared.entity.Shared;
import com.mjc.groupware.shared.repository.SharedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SharedService {

    private final SharedRepository repository;
    private final MemberRepository memberRepository; // 추가

    public int createSharedApi(SharedDto dto) {
        // memberNo로 Member 객체 조회
        Member member = memberRepository.findById(dto.getMember_no())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + dto.getMember_no()));

        // Shared 엔티티 생성
        Shared entity = dto.toEntity();
        entity = Shared.builder()
                .sharedTitle(entity.getSharedTitle())
                .sharedContent(entity.getSharedContent())
                .views(entity.getViews() != 0 ? entity.getViews() : 0)
                .member(member) // 조회한 Member 객체 설정
                .build();

        Shared saved = repository.save(entity);
        return saved != null ? 1 : 0;
    }

    // 게시글 목록 조회
    public List<Shared> getSharedList() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "sharedNo"));
    }
    
    // 게시글 상세 조회 (조회수 증가 포함)
    public Shared getSharedDetail(Long sharedNo) {
        Shared shared = repository.findById(sharedNo).orElse(null);
        if (shared != null) {
            // 조회수 증가
            shared = Shared.builder()
                    .sharedNo(shared.getSharedNo())
                    .sharedTitle(shared.getSharedTitle())
                    .sharedContent(shared.getSharedContent())
                    .views(shared.getViews() + 1) // 조회수 1 증가
                    .member(shared.getMember())
                    .regDate(shared.getRegDate())
                    .modDate(shared.getModDate())
                    .build();
            repository.save(shared); // 업데이트된 조회수 저장
        }
        return shared;
    }
}
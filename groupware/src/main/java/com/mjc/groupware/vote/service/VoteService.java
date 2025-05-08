package com.mjc.groupware.vote.service;

import com.mjc.groupware.vote.dto.VoteDto;
import com.mjc.groupware.vote.dto.VoteOptionDto;
import com.mjc.groupware.vote.entity.Vote;
import com.mjc.groupware.vote.entity.VoteOption;
import com.mjc.groupware.vote.entity.VoteResult;
import com.mjc.groupware.vote.repository.VoteOptionRepository;
import com.mjc.groupware.vote.repository.VoteRepository;
import com.mjc.groupware.vote.repository.VoteResultRepository;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepo;
    private final VoteOptionRepository optionRepo;
    private final VoteResultRepository resultRepo;
    private final MemberRepository memberRepo;

    /**
     * 투표 생성
     */
    @Transactional
    public Long createVote(VoteDto dto, List<VoteOptionDto> options) {
        Vote vote = new Vote();
        vote.setVoteTitle(dto.getVote_title());
        vote.setIsMultiple(dto.getIs_multiple());
        vote.setIsAnonymous(dto.getIs_anonymous());
        vote.setEndDate(dto.getEnd_date());
        vote.setRegDate(LocalDateTime.now());

        vote = voteRepo.save(vote);

        for (VoteOptionDto opt : options) {
            VoteOption option = new VoteOption();
            option.setVote(vote);
            option.setOptionText(opt.getOption_text());
            option.setOrderNo(opt.getOrder_no());

            optionRepo.save(option);
        }

        return vote.getVoteNo();
    }

    /**
     * 단일 투표 조회
     */
    public Optional<VoteDto> getVote(Long voteNo) {
        return voteRepo.findById(voteNo).map(v -> {
            VoteDto dto = new VoteDto();
            dto.setVote_no(v.getVoteNo());
            dto.setVote_title(v.getVoteTitle());
            dto.setIs_multiple(v.getIsMultiple());
            dto.setIs_anonymous(v.getIsAnonymous());
            dto.setEnd_date(v.getEndDate());
            dto.setReg_date(v.getRegDate());
            return dto;
        });
    }

    /**
     * 전체 투표 목록 조회
     */
    public List<VoteDto> getAllVotes() {
        return voteRepo.findAll().stream().map(v -> {
            VoteDto dto = new VoteDto();
            dto.setVote_no(v.getVoteNo());
            dto.setVote_title(v.getVoteTitle());
            dto.setIs_multiple(v.getIsMultiple());
            dto.setIs_anonymous(v.getIsAnonymous());
            dto.setEnd_date(v.getEndDate());
            dto.setReg_date(v.getRegDate());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 투표 수정
     */
    @Transactional
    public void updateVote(Long voteNo, VoteDto dto) {
        voteRepo.findById(voteNo).ifPresent(v -> {
            v.setVoteTitle(dto.getVote_title());
            v.setIsMultiple(dto.getIs_multiple());
            v.setIsAnonymous(dto.getIs_anonymous());
            v.setEndDate(dto.getEnd_date());
        });
    }

    /**
     * 투표 삭제
     */
    @Transactional
    public boolean deleteVoteByBoardNo(Long boardNo) {
        Optional<Vote> voteOpt = voteRepo.findByBoard_BoardNo(boardNo);
        if (voteOpt.isPresent()) {
            Vote vote = voteOpt.get();
            if (vote.getBoard() != null) {
                vote.getBoard().setVote(null); // 💥 양방향 연관관계 끊기 (중요)
            }
            voteRepo.delete(vote);
            return true;
        }
        return false;
    }
    /**
     * 투표 참여
     */
    @Transactional
    public void participate(Long voteNo, List<Long> optionNos, Long memberNo) {
        Vote vote = voteRepo.findById(voteNo)
                .orElseThrow(() -> new IllegalArgumentException("투표가 존재하지 않습니다."));

        Member member = memberRepo.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        for (Long optNo : optionNos) {
            VoteOption option = optionRepo.findById(optNo)
                    .orElseThrow(() -> new IllegalArgumentException("선택지가 존재하지 않습니다."));

            VoteResult result = new VoteResult();
            result.setVote(vote);
            result.setOption(option);
            result.setMember(member);
            result.setVoteTime(LocalDateTime.now());

            resultRepo.save(result);
        }
    }
    
    
    // 투표 차트 기능 추가
    public List<Map<String, Object>> getVoteResultForChart(Long voteNo) {
        List<VoteOption> options = optionRepo.findByVote_VoteNo(voteNo);
        List<Map<String, Object>> result = new ArrayList<>();

        // 기본 득표 수 구성
        for (VoteOption option : options) {
            int count = resultRepo.countByOption_OptionNo(option.getOptionNo());
            Map<String, Object> map = new HashMap<>();
            map.put("optionText", option.getOptionText());
            map.put("voteCount", count);
            map.put("anonymous", option.getVote().getIsAnonymous()); // JS에서 익명 여부 체크용
            result.add(map);
        }

        // 실명일 경우만 투표자 명단 추가
        Vote vote = voteRepo.findById(voteNo).orElseThrow();
        if ("N".equals(vote.getIsAnonymous())) {
            List<VoteResult> results = resultRepo.findByVote_VoteNo(voteNo);
            for (Map<String, Object> map : result) {
                String optionText = (String) map.get("optionText");
                List<String> voters = results.stream()
                        .filter(r -> r.getOption().getOptionText().equals(optionText))
                        .map(r -> {
                            Member m = r.getMember();
                            String dept = m.getDept() != null ? m.getDept().getDeptName() : "부서없음";
                            return "[" + dept + "]" + m.getMemberName();
                        })
                        .collect(Collectors.toList());
                map.put("voters", voters);
            }
        }

        return result;
    }
    

    // 투표가 마감되었는지 확인
    public boolean isVoteClosed(Long voteNo) {
        return voteRepo.findById(voteNo)
                .map(vote -> vote.getEndDate().isBefore(LocalDateTime.now()))
                .orElse(true); // 없는 경우 마감된 것으로 처리
    }
    
    // 이미 투표했는지 여부 확인
    public boolean hasUserAlreadyVoted(Long voteNo, Long memberNo) {
        return resultRepo.existsByVote_VoteNoAndMember_MemberNo(voteNo, memberNo);
    }
    
}
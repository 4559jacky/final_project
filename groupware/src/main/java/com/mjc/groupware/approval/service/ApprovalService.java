package com.mjc.groupware.approval.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.approval.dto.ApprAgreementerDto;
import com.mjc.groupware.approval.dto.ApprApproverDto;
import com.mjc.groupware.approval.dto.ApprReferencerDto;
import com.mjc.groupware.approval.dto.ApprovalDto;
import com.mjc.groupware.approval.dto.ApprovalFormDto;
import com.mjc.groupware.approval.dto.PageDto;
import com.mjc.groupware.approval.dto.SearchDto;
import com.mjc.groupware.approval.entity.ApprAgreementer;
import com.mjc.groupware.approval.entity.ApprApprover;
import com.mjc.groupware.approval.entity.ApprReferencer;
import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.approval.entity.ApprovalForm;
import com.mjc.groupware.approval.mybatis.mapper.ApprovalMapper;
import com.mjc.groupware.approval.mybatis.vo.ApprovalStatusVo;
import com.mjc.groupware.approval.mybatis.vo.ApprovalVo;
import com.mjc.groupware.approval.repository.ApprAgreementerRepository;
import com.mjc.groupware.approval.repository.ApprApproverRepository;
import com.mjc.groupware.approval.repository.ApprReferencerRepository;
import com.mjc.groupware.approval.repository.ApprovalFormRepository;
import com.mjc.groupware.approval.repository.ApprovalRepository;
import com.mjc.groupware.approval.specification.ApprovalSpecification;
import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.plan.dto.PlanDto;
import com.mjc.groupware.plan.entity.Plan;
import com.mjc.groupware.plan.repository.PlanRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ApprovalService {

	private final ApprovalFormRepository approvalFormRepository;
	private final ApprovalRepository approvalRepository;
	private final ApprApproverRepository apprApproverRepository;
	private final ApprAgreementerRepository apprAgreementerRepository;
	private final ApprReferencerRepository apprReferencerRepository;
	private final PlanRepository planRepository;
	private final MemberRepository memberRepository;
	private final ApprovalMapper approvalMapper;

	public int createApprovalApi(ApprovalFormDto dto) {
		int result = 0;
		try {
			ApprovalForm entity = dto.toEntity();
			ApprovalForm saved = approvalFormRepository.save(entity);
			if(saved != null) {
				result = 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<ApprovalForm> selectApprovalFormAll() {
		
		List<ApprovalForm> resultList = approvalFormRepository.findAll();
		
		return resultList;
	}

	public ApprovalFormDto selectApprovalFormById(Long id) {
		
		ApprovalForm entity = approvalFormRepository.findById(id).orElse(null);
		ApprovalFormDto dto = new ApprovalFormDto().toDto(entity);
		
		return dto;
	}

	public int updateApprovalFormStatus(Long id) {
		int result = 0;
		try {
			
			ApprovalForm entity = approvalFormRepository.findById(id).orElse(null);
			ApprovalFormDto dto = new ApprovalFormDto().toDto(entity);
			
			if("Y".equals(dto.getApproval_form_status())) {
				dto.setApproval_form_status("N");
			} else {
				dto.setApproval_form_status("Y");
			}
			
			ApprovalForm param = dto.toEntity();
			approvalFormRepository.save(param);
			
			result = 1;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// 결재 승인 요청
	@Transactional(rollbackFor = Exception.class)
	public int createApprovalApi(ApprovalDto approvalDto) {
		int result = 0;
		
		try {
			
			if(approvalDto.getAgreementer_no() != null) { 
				approvalDto.setAppr_status("A");
			} else {
				approvalDto.setAppr_status("D");
				approvalDto.setAppr_order_status(1);
			}
			
			Approval saved = approvalRepository.save(approvalDto.toEntity());
			
			Long apprNo = saved.getApprNo();
			
			ApprApproverDto approverDto = new ApprApproverDto();
			ApprAgreementerDto agreementerDto = new ApprAgreementerDto();
			ApprReferencerDto referencerDto = new ApprReferencerDto();
			
			// 결재자
			approverDto.setAppr_no(apprNo);
			approverDto.setApprovers(approvalDto.getApprover_no());
			if(approvalDto.getApprover_no().size() < 1) {
				// 예외처리 발생
				
			}
			List<ApprApprover> approverList = approverDto.toEntityList();
			for(ApprApprover entity : approverList) {
				try {
					apprApproverRepository.save(entity);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			// 합의자
			agreementerDto.setAppr_no(apprNo);
			if(approvalDto.getAgreementer_no() != null) {
				agreementerDto.setAgreementers(approvalDto.getAgreementer_no());
				List<ApprAgreementer> agreementerList = agreementerDto.toEntityList();
				for(ApprAgreementer entity : agreementerList) {
					try {
						apprAgreementerRepository.save(entity);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			
			// 참조자
			referencerDto.setAppr_no(apprNo);
			if(approvalDto.getReferencer_no() != null) {
				referencerDto.setReferencers(approvalDto.getReferencer_no());
				List<ApprReferencer> referencerList = referencerDto.toEntityList();
				for(ApprReferencer entity : referencerList) {
					try {
						apprReferencerRepository.save(entity);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}

			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return result;
		
	}

	// 결재리스트 받아오기 - 보낸 문서함 출력(검색, 페이징 O)
	public Page<Approval> selectApprovalAll(MemberDto member, SearchDto searchDto, PageDto pageDto) {
		Pageable pageable = PageRequest.of(pageDto.getNowPage() -1 , pageDto.getNumPerPage(),
				Sort.by("apprRegDate").descending());
		
		if(searchDto.getOrder_type() == 2) {
			pageable = PageRequest.of(pageDto.getNowPage() -1 , pageDto.getNumPerPage(),
					Sort.by("apprRegDate").ascending());
		}
		
		Specification<Approval> spec = (root, query, criteriaBuilder) -> null;
		if(searchDto.getSearch_status() == null) {
			if("appr_title".equals(searchDto.getSearch_type())) {
				spec = spec.and(ApprovalSpecification.approvalTitleContains(searchDto.getSearch_text()))
						.and(ApprovalSpecification.approvalSenderContains(member.getMember_no()));
			} else if("approval_form_name".equals(searchDto.getSearch_type())) {
				spec = spec.and(ApprovalSpecification.approvalFormNameContains(searchDto.getSearch_text()))
						.and(ApprovalSpecification.approvalSenderContains(member.getMember_no()));
			} else {
				spec = spec.and(ApprovalSpecification.approvalSenderContains(member.getMember_no()));
			}
		} else {
			if("appr_title".equals(searchDto.getSearch_type())) {
				spec = spec.and(ApprovalSpecification.approvalTitleContains(searchDto.getSearch_text()))
						.and(ApprovalSpecification.approvalStatusContains(searchDto.getSearch_status()))
						.and(ApprovalSpecification.approvalSenderContains(member.getMember_no()));
			} else if("approval_form_name".equals(searchDto.getSearch_type())) {
				spec = spec.and(ApprovalSpecification.approvalFormNameContains(searchDto.getSearch_text()))
						.and(ApprovalSpecification.approvalStatusContains(searchDto.getSearch_status()))
						.and(ApprovalSpecification.approvalSenderContains(member.getMember_no()));
			} else {
				spec = spec.and(ApprovalSpecification.approvalStatusContains(searchDto.getSearch_status()))
						.and(ApprovalSpecification.approvalSenderContains(member.getMember_no()));
			}
		}
		
		
		Page<Approval> list = approvalRepository.findAll(spec, pageable);
		
		return list;
	}
	
	// 결재리스트 받아오기 - 보낸 문서함 출력 (검색X)
	public List<Approval> selectApprovalAllById(MemberDto member) {
		
		List<Approval> approvalList = new ArrayList<Approval>();
		
		approvalList = approvalRepository.findAllByMember_MemberNo(member.getMember_no());
		
		return approvalList;
	}
	
	// 결재리스트 받아오기 - 받은 문서함 출력
	public List<ApprovalVo> selectApprovalAllByApproverId(MemberDto member, SearchDto searchDto, PageDto pageDto) {
		List<ApprovalVo> approvalVoList = new ArrayList<ApprovalVo>();
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		
		paramMap.put("member_no", member.getMember_no());
		paramMap.put("search_type", searchDto.getSearch_type());
		paramMap.put("search_text", searchDto.getSearch_text());
		paramMap.put("order_type", searchDto.getOrder_type());
		paramMap.put("search_status", searchDto.getSearch_status());
		
		approvalVoList = approvalMapper.selectApprovalAllByMemberNo(paramMap);

		return approvalVoList;
	}
	
	public ApprovalStatusVo selectApprovalStatusByApproverId(MemberDto member) {
		ApprovalStatusVo approvalStatusVo = new ApprovalStatusVo();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("member_no", member.getMember_no());
		
		approvalStatusVo = approvalMapper.selectApprovalStatusByMemberNo(paramMap);
		
		return approvalStatusVo;
	}

	public Approval selectApprovalOneByApprovalNo(Long id) {
		Approval approval = approvalRepository.findById(id).orElse(null);
		return approval;
	}

	public List<ApprApprover> selectApprApproverAllByApprovalNo(Long id) {
		List<ApprApprover> approverList = apprApproverRepository.findAllByApproval_ApprNo(id);
		return approverList;
	}
	
	public List<ApprAgreementer> selectApprAgreementerAllByApprovalNo(Long id) {
		List<ApprAgreementer> agreementerList = apprAgreementerRepository.findAllByApproval_ApprNo(id);
		return agreementerList;
	}

	public List<ApprReferencer> selectApprReferencerAllByApprovalNo(Long id) {
		List<ApprReferencer> referencerList = apprReferencerRepository.findAllByApproval_ApprNo(id);
		return referencerList;
	}
	
	// 결재자 - 결재 승인(Dto에 Setter)
	@Transactional(rollbackFor = Exception.class)
	public int approvalSuccessApi(Long id, MemberDto member) {
		
		int result = 0;
		
		try {
			// 결재와 결재자를 찾음
			Approval approval = approvalRepository.findById(id).orElse(null);
			ApprApprover approver = apprApproverRepository.findByMember_MemberNoAndApproval_ApprNo(member.getMember_no(), id);
			
			// 결재의 순서와 결재자의 순서가 같을 때
			if(approval.getApprOrderStatus() == approver.getApproverOrder()) {
				
				ApprovalDto approvalDto = new ApprovalDto().toDto(approval);
				ApprApproverDto approverDto = new ApprApproverDto().toDto(approver);
				approvalDto.setAppr_order_status(approval.getApprOrderStatus()+1);
				approverDto.setApprover_decision_status("C");
				
				Approval approvalParam = approvalDto.toEntity();
				ApprApprover approverParam = approverDto.toEntity();
				
				Approval approvalEntity = approvalRepository.save(approvalParam);
				ApprApprover approverEntity = apprApproverRepository.save(approverParam);
				
				
				// 모든 결재자맵핑 데이터 찾기
				List<ApprApprover> approverList = apprApproverRepository.findAllByApproval_ApprNo(id);
				
				boolean vali = false;
				int max = 0;
				
				for(ApprApprover a : approverList) {
					if(max < a.getApproverOrder()) {
						max = a.getApproverOrder();
					}
				}
				
				// 모든 결재자보다 결재순서가 더 높으면
				if(max < approvalEntity.getApprOrderStatus()) {
					vali = true;
				}
				
				// 최종 결재자가 승인을 했을 때
				if(vali == true) {
					ApprovalDto approvalDto2 = new ApprovalDto().toDto(approvalEntity);
					approvalDto2.setAppr_status("C");
					approvalDto2.setAppr_res_date(approverEntity.getApproverDecisionStatusTime());
					
					Approval approvalParam2 = approvalDto2.toEntity();
					if(approval.getApprovalForm().getApprovalFormType() == 1) {
						// 결재 최종 승인
						if(approval.getMember().getAnnualLeave() - approval.getUseAnnualLeave() >= 0) {
							approvalRepository.save(approvalParam2);
						}
					} else {
						approvalRepository.save(approvalParam2);
					}
					
					// 연차결재 승인 시 휴가 일정에 반영될 수 있도록 휴가 일정 데이터 저장
						
						
					// Trigger 사용으로 대체
//						PlanDto planDto = new PlanDto();
//						
//						String annualLeaveType = "";
//						LocalDate start_date = approvalParam2.getStartDate();
//						LocalDate end_date = approvalParam2.getEndDate();
//						
//						if(approvalParam2.getAnnualLeaveType() == 0) {
//							annualLeaveType = "연차";
//							planDto.setStart_date(start_date != null ? start_date.atTime(9, 0, 0) : null);
//							planDto.setEnd_date(end_date != null ? end_date.atTime(18, 0, 0) : null);
//						} else if(approvalParam2.getAnnualLeaveType() == 1) {
//							annualLeaveType = "오전반차";
//							planDto.setStart_date(start_date != null ? start_date.atTime(9, 0, 0) : null);
//							planDto.setEnd_date(end_date != null ? end_date.atTime(13, 0, 0) : null);
//						} else if(approvalParam2.getAnnualLeaveType() == 2) {
//							annualLeaveType = "오후반차";
//							planDto.setStart_date(start_date != null ? start_date.atTime(14, 0, 0) : null);
//							planDto.setEnd_date(end_date != null ? end_date.atTime(18, 0, 0) : null);
//						}
//						
//						planDto.setPlan_title(approval.getMember().getMemberName()+"["+annualLeaveType+"]");
//						planDto.setPlan_content(approval.getMember().getMemberName()+"["+annualLeaveType+"]");
//						planDto.setPlan_type("휴가");
//						planDto.setReg_member_no(approval.getMember().getMemberNo());
//						
//						Plan planEntity = planDto.toEntity();
//						
//						planRepository.save(planEntity);
//						
//						Member mem = memberRepository.findById(approval.getMember().getMemberNo()).orElse(null);
//						MemberDto md = new MemberDto().toDto(mem);
//						md.setAnnual_leave(md.getAnnual_leave()-approval.getUseAnnualLeave());
//						
//						Member memberEntity = md.toEntity();
//						
//						memberRepository.save(memberEntity);
//					}
				}
			}
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	// 결재자 - 결재 반려(Dto에 Setter)
	public int approvalFailApi(Long id, String reason, MemberDto member) {
		int result = 0;
		
		try {
			// 결재자맵핑 데이터를 찾아서 상태변경
			ApprApprover approver = apprApproverRepository.findByMember_MemberNoAndApproval_ApprNo(member.getMember_no(), id);
			
			ApprApproverDto approverDto = new ApprApproverDto().toDto(approver);
			
			approverDto.setApprover_decision_status("R");
			approverDto.setDecision_reason(reason);
			
			ApprApprover approvalParam = approverDto.toEntity();
			
			ApprApprover approverEntity = apprApproverRepository.save(approvalParam);
			
			Approval approval = approvalRepository.findById(id).orElse(null);
			
			ApprovalDto approvalDto = new ApprovalDto().toDto(approval);
			
			approvalDto.setAppr_status("R");
			approvalDto.setAppr_res_date(approverEntity.getApproverDecisionStatusTime());
			approvalDto.setAppr_reason(approverEntity.getDecisionReason());
			
			Approval approvalEntity = approvalDto.toEntity();
			
			approvalRepository.save(approvalEntity);
			
			result = 1;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	// 합의자 - 결재 수락(Dto에 setter)
	@Transactional(rollbackFor = Exception.class)
	public int approvalAgreeApi(Long id, MemberDto member) {
		
		int result = 0;
		
		try {
			
			// 합의자맵핑 데이터를 찾아서 상태변경
			ApprAgreementer agreementer = apprAgreementerRepository.findByMember_MemberNoAndApproval_ApprNo(member.getMember_no(), id);
			ApprAgreementerDto agreementerDto = new ApprAgreementerDto().toDto(agreementer);
			agreementerDto.setAgreementer_agree_status("C");
			
			ApprAgreementer agreementerParam = agreementerDto.toEntity();
			
			apprAgreementerRepository.save(agreementerParam);
			
			// 모든 합의자맵핑 데이터 찾기
			List<ApprAgreementer> agreementerList = apprAgreementerRepository.findAllByApproval_ApprNo(id);
			
			int checkAgreeStatus = 0;
			
			if(agreementerList != null) {
				
				// 모든 합의자가 동의("C")일 시 결재 상태변경
				for(ApprAgreementer a : agreementerList) {
					if("W".equals(a.getAgreementerAgreeStatus())) {
						checkAgreeStatus = 1;
					}
				}
				
				// checkAgreeStatus가 0이면 모든 합의자가 동의 -> 결재 순서상태 1로 변환
				if(checkAgreeStatus == 0) {
					Approval approval = approvalRepository.findById(id).orElse(null);
					ApprovalDto approvalDto = new ApprovalDto().toDto(approval);
					approvalDto.setAppr_order_status(1);
					approvalDto.setAppr_status("D");
					
					Approval approvalParam = approvalDto.toEntity();
					
					approvalRepository.save(approvalParam);
				}
			}
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}

	
	// 합의자 - 결재 거절(Dto에 Setter)
	public int approvalRejectApi(Long id, String reason, MemberDto member) {
		
		int result = 0;
		
		try {
			
			// 합의자맵핑 데이터를 찾아서 상태변경
			ApprAgreementer agreementer = apprAgreementerRepository.findByMember_MemberNoAndApproval_ApprNo(member.getMember_no(), id);
			ApprAgreementerDto agreementerDto = new ApprAgreementerDto().toDto(agreementer);
			
			agreementerDto.setAgreementer_agree_status("R");
			agreementerDto.setAgree_reason(reason);
			
			ApprAgreementer agreementerParam = agreementerDto.toEntity();
			
			ApprAgreementer entity = apprAgreementerRepository.save(agreementerParam);
			
			Approval approval = approvalRepository.findById(id).orElse(null);
			ApprovalDto approvalDto = new ApprovalDto().toDto(approval);
			approvalDto.setAppr_status("R");
			approvalDto.setAppr_res_date(entity.getAgreementerAgreeStatusTime());
			approvalDto.setAppr_reason(entity.getAgreeReason());
			
			Approval approvalParam = approvalDto.toEntity();
			
			approvalRepository.save(approvalParam);
			
			result = 1;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}

package com.mjc.groupware.approval.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
import com.mjc.groupware.member.repository.MemberRepository;
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
	private final ApprovalAttachService approvalAttachService;
	
	// 알람
	private final ApprovalAlarmService approvalAlarmService;

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
	public int createApprovalApi(ApprovalDto approvalDto , List<MultipartFile> files) {
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
			
			 if (files != null && !files.isEmpty()) {
		            for (MultipartFile file : files) {
		                if (!file.isEmpty()) {
		                    try {
		                    	approvalAttachService.saveFile(file, saved);
		                    } catch (IOException e) {
		                        e.printStackTrace();
		                        return 0;
		                    }
		                }
		            }
		        }
			 
			 // 결재 알림 보내기
			 Set<Long> targetMemberNoSet = new HashSet<>();
			 
			 boolean hasAgreementers = approvalDto.getAgreementer_no() != null && !approvalDto.getAgreementer_no().isEmpty();
			 boolean hasReferencers = approvalDto.getReferencer_no() != null && !approvalDto.getReferencer_no().isEmpty();

			// 1합의자
			if (hasAgreementers) {
			    targetMemberNoSet.addAll(approvalDto.getAgreementer_no());
			}

			// 2참조자
			if (hasReferencers) {
			    targetMemberNoSet.addAll(approvalDto.getReferencer_no());
			}

			// 결재자 (합의자 없을 때만)
			if (!hasAgreementers && approverList != null && !approverList.isEmpty()) {
			    for (ApprApprover a : approverList) {
			        if (a.getApproverOrder() == 1 &&
			            a.getMember() != null &&
			            a.getMember().getMemberNo() != null) {
			            targetMemberNoSet.add(a.getMember().getMemberNo());
			        }
			    }
			}
			
			System.out.println("🔔 합의자 번호: " + approvalDto.getAgreementer_no());
			System.out.println("🔔 참조자 번호: " + approvalDto.getReferencer_no());
			System.out.println("🔔 결재자 번호: " + approvalDto.getApprover_no());

			// List로 변환해서 전송
			List<Long> targetMemberNos = new ArrayList<>(targetMemberNoSet);
			approvalAlarmService.sendAlarmToMembers(
			    targetMemberNos,
			    saved,
			    "새로운 결재가 도착하였습니다."
			);

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
	        Approval approval = approvalRepository.findById(id).orElse(null);
	        if (approval == null) return 0;

	        Approval parentApproval = approval.getParentApproval() != null
	                ? approvalRepository.findById(approval.getParentApproval().getApprNo()).orElse(null)
	                : null;

	        ApprApprover approver = apprApproverRepository.findByMember_MemberNoAndApproval_ApprNo(member.getMember_no(), id);

	        if (approval.getApprOrderStatus() == approver.getApproverOrder()) {

	            ApprovalDto approvalDto = new ApprovalDto().toDto(approval);
	            ApprApproverDto approverDto = new ApprApproverDto().toDto(approver);

	            approvalDto.setAppr_order_status(approval.getApprOrderStatus() + 1);
	            approverDto.setApprover_decision_status("C");

	            Approval approvalParam = approvalDto.toEntity(parentApproval);
	            ApprApprover approverParam = approverDto.toEntity();

	            Approval approvalEntity = approvalRepository.save(approvalParam);
	            ApprApprover approverEntity = apprApproverRepository.save(approverParam);

	            List<ApprApprover> approverList = apprApproverRepository.findAllByApproval_ApprNo(id);
	            boolean vali = false;
	            int max = approverList.stream().mapToInt(ApprApprover::getApproverOrder).max().orElse(0);

	            if (max < approvalEntity.getApprOrderStatus()) {
	                vali = true;
	            }

	            if (vali) {
	                ApprovalDto approvalDto2 = new ApprovalDto().toDto(approvalEntity);
	                approvalDto2.setAppr_status("C");
	                approvalDto2.setAppr_res_date(approverEntity.getApproverDecisionStatusTime());

	                Approval approvalParam2 = approvalDto2.toEntity(parentApproval);
	                approvalRepository.save(approvalParam2);
	            }
	        }

	        result = 1;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return result;
	}

	
	
	// 결재자 - 결재 반려(Dto에 Setter)
	@Transactional(rollbackFor = Exception.class)
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
			Approval parentApproval = approval.getParentApproval() != null
	                ? approvalRepository.findById(approval.getParentApproval().getApprNo()).orElse(null)
	                : null;
			
			ApprovalDto approvalDto = new ApprovalDto().toDto(approval);
			
			approvalDto.setAppr_status("R");
			approvalDto.setAppr_res_date(approverEntity.getApproverDecisionStatusTime());
			approvalDto.setAppr_reason(approverEntity.getDecisionReason());
			
			
			Approval approvalEntity = approvalDto.toEntity(parentApproval);
			
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
	@Transactional(rollbackFor = Exception.class)
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
	
	
	// 결재 회수
	@Transactional(rollbackFor = Exception.class)
	public int approvalReturnApi(Long id, String reason) {
		
		int result = 0;
		
		try {
			
			Approval approvalParam = approvalRepository.findById(id).orElse(null);
			List<ApprApprover> approvers = apprApproverRepository.findAllByApproval_ApprNo(approvalParam.getApprNo());
			int lastApproverId = 0;
			ApprApprover approver = null;
			for(ApprApprover a : approvers) {
				if(lastApproverId < a.getApproverOrder()) {
					lastApproverId = a.getApproverOrder();
				}
			}
			for(ApprApprover a : approvers) {
				if(lastApproverId == a.getApproverOrder()) {
					approver = a;
				}
			}
			System.out.println("dto 저장전 : "+reason);
			// 결재자 찾음
			// 결재를 재생성 하고, 결재자(ApprApprover) 재생성
			ApprovalDto approvalDto = ApprovalDto.builder()
					.appr_title("[결재 회수]"+approvalParam.getApprTitle())
					.appr_text(approvalParam.getApprText())
					.appr_status("D")
					.appr_order_status(1)
					.start_date(approvalParam.getStartDate())
					.end_date(approvalParam.getEndDate())
					.use_annual_leave(approvalParam.getUseAnnualLeave())
					.annual_leave_type(approvalParam.getAnnualLeaveType())
					.return_reason(reason)
					.appr_sender(approvalParam.getMember().getMemberNo())
					.approval_type_no(approvalParam.getApprovalForm().getApprovalFormNo())
					.parent_approval(approvalParam.getApprNo())
					.build();
			System.out.println("dto 저장후 : "+approvalDto.getReturn_reason());
			Approval newApproval = approvalDto.toEntity(approvalParam);
			
			System.out.println("entity 저장후 : "+newApproval.getReturnReason());
			Approval entity = approvalRepository.save(newApproval);
			System.out.println("데이터 저장후 : "+entity.getReturnReason());
			
			ApprApproverDto apprApproverDto = ApprApproverDto.builder()
					.approver_order(1)
					.approver_decision_status("W")
					.appr_no(entity.getApprNo())
					.approver(approver.getMember().getMemberNo())
					.build();
			
			ApprApprover approverEntity = apprApproverDto.toEntity();
			apprApproverRepository.save(approverEntity);
			
			result = 1;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	
	// 결재 중 회수 대기나 회수 승인이 있는 결재
	public int selectReturnApprovalByApprovalNo(Long id) {
		
		int result = 0;
		
		List<Approval> childApproval = approvalRepository.findAllByParentApproval_ApprNo(id);
//		Specification<Approval> spec = (root, query, criteriaBuilder) -> null;
//		spec = spec.and(ApprovalSpecification.approvalReturnApprovalContains(id));
//
//		childApproval = approvalRepository.findAll(spec);
		
		
		for(Approval a : childApproval) {
			System.out.println("자식결재 : "+a.getApprNo());
		}
		
		if(childApproval != null) {
			for(Approval a : childApproval) {
				if("C".equals(a.getApprStatus()) || "D".equals(a.getApprStatus())) {
					result = 1;
				}
			}
		}
		
		// result가 1이면 회수버튼이 생기면 안되고 0일 때 생겨야함
		return result;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public int retryApprovalApi(ApprovalDto approvalDto, List<MultipartFile> files, List<Long> deleteFiles) {
		int result = 0;
		
		try {
			// 이전 결재 entity
			Approval entity = approvalRepository.findById(approvalDto.getAppr_no()).orElse(null);
			// 합의자 여부 확인
			List<ApprAgreementer> agreementers = apprAgreementerRepository.findAllByApproval_ApprNo(entity.getApprNo());
			
			
			// 합의자가 있으면 합의자 먼저, 없으면 바로 결재자로
			if(agreementers.size() != 0) {
				approvalDto.setAppr_status("A");
				approvalDto.setAppr_order_status(0);
			} else {
				approvalDto.setAppr_status("D");
				approvalDto.setAppr_order_status(1);
			}
			
			approvalDto.setAppr_res_date(null);
			approvalDto.setAppr_reason(null);
			approvalDto.setAppr_reg_date(LocalDateTime.now());
			
			Approval newEntity = approvalDto.toEntity();
			
			approvalRepository.save(newEntity);
			
			if (deleteFiles != null) {
				System.out.println("test");
		        for (Long id : deleteFiles) {
		            try {
		                approvalAttachService.deleteAttachById(id);  // 실제 삭제
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
		        }
		    }
			
			if (files != null && !files.isEmpty()) {
		        for (MultipartFile file : files) {
		            if (!file.isEmpty()) {
		                try {
		                	approvalAttachService.saveFile(file, newEntity);
		                } catch (IOException e) {
		                    e.printStackTrace();
		                    return 0;
		                }
		            }
		        }
		    }
			
			result = 1;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}

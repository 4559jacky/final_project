package com.mjc.groupware.approval.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.approval.dto.ApprAgreementerDto;
import com.mjc.groupware.approval.dto.ApprApproverDto;
import com.mjc.groupware.approval.dto.ApprReferencerDto;
import com.mjc.groupware.approval.dto.ApprovalDto;
import com.mjc.groupware.approval.dto.ApprovalFormDto;
import com.mjc.groupware.approval.entity.ApprAgreementer;
import com.mjc.groupware.approval.entity.ApprApprover;
import com.mjc.groupware.approval.entity.ApprReferencer;
import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.approval.entity.ApprovalForm;
import com.mjc.groupware.approval.mybatis.mapper.ApprovalMapper;
import com.mjc.groupware.approval.mybatis.vo.ApprovalVo;
import com.mjc.groupware.approval.repository.ApprAgreementerRepository;
import com.mjc.groupware.approval.repository.ApprApproverRepository;
import com.mjc.groupware.approval.repository.ApprReferencerRepository;
import com.mjc.groupware.approval.repository.ApprovalFormRepository;
import com.mjc.groupware.approval.repository.ApprovalRepository;
import com.mjc.groupware.approval.vo.ApprApproverVo;
import com.mjc.groupware.member.dto.MemberDto;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ApprovalService {

	private final ApprovalFormRepository approvalFormRepository;
	private final ApprovalRepository approvalRepository;
	private final ApprApproverRepository apprApproverRepository;
	private final ApprAgreementerRepository apprAgreementerRepository;
	private final ApprReferencerRepository apprReferencerRepository;
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
				agreementerDto.setAgreementer_no(approvalDto.getAgreementer_no());
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
				referencerDto.setReferencer_no(approvalDto.getReferencer_no());
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

	public List<Approval> selectApprovalAllById(MemberDto member) {
		
		List<Approval> approvalList = new ArrayList<Approval>();
		
		approvalList = approvalRepository.findAllByMember_MemberNo(member.getMember_no());
		
		return approvalList;
	}
	
	// 결재리스트 받아오기 - 받은 문서함 출력
	public List<ApprovalVo> selectApprovalAllByApproverId(MemberDto member) {
		List<ApprovalVo> approvalVoList = new ArrayList<ApprovalVo>();
		approvalVoList = approvalMapper.selectApprovalAllByMemberNo(member.getMember_no());

		// List<ApprApprover> approverMappingList = new ArrayList<ApprApprover>();
		// approverMappingList = apprApproverRepository.findAllByMember_MemberNo(member.getMember_no());
		
		return approvalVoList;
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
	
	// 결재자 - 결재 승인
	@Transactional(rollbackFor = Exception.class)
	public int approvalSuccessApi(Long id, MemberDto member) {
		
		int result = 0;
		
		try {
			// 결재와 결재자를 찾음
			Approval approval = approvalRepository.findById(id).orElse(null);
			ApprApprover approver = apprApproverRepository.findByMember_MemberNoAndApproval_ApprNo(member.getMember_no(), id);
			
			// 결재의 순서와 결재자의 순서가 같을 때
			if(approval.getApprOrderStatus() == approver.getApproverOrder()) {
				approval.setApprOrderStatus(approval.getApprOrderStatus()+1);
				approver.setApproverDecisionStatus("C");
				
				Approval approvalEntity = approvalRepository.save(approval);
				apprApproverRepository.save(approver);
				
				
				// 모든 합의자맵핑 데이터 찾기
				List<ApprApprover> approverList = apprApproverRepository.findAllByApproval_ApprNo(id);
				
				boolean vali = true;
				int max = 0;
				
				for(ApprApprover a : approverList) {
					if(max < a.getApproverOrder()) {
						max = a.getApproverOrder();
					}
				}
				
				if(max < approvalEntity.getApprOrderStatus()) {
					vali = false;
				}
				
				if(vali == false) {
					approval.setApprStatus("C");
					approvalRepository.save(approval);
				}
				
			}
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	// 합의자 - 결재 수락
	@Transactional(rollbackFor = Exception.class)
	public int approvalAgreeApi(Long id, MemberDto member) {
		
		int result = 0;
		
		try {
			
			// 합의자맵핑 데이터를 찾아서 상태변경
			ApprAgreementer agreementer = apprAgreementerRepository.findByMember_MemberNoAndApproval_ApprNo(member.getMember_no(), id);
			
			agreementer.setAgreementerAgreeStatus("C");
			apprAgreementerRepository.save(agreementer);
			
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
					approval.setApprOrderStatus(1);
					approval.setApprStatus("D");
					approvalRepository.save(approval);
				}
			}
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return result;
	}

	public int approvalRejectApi(Long id, String reason, MemberDto member) {
		
		int result = 0;
		
		try {
			
			// 합의자맵핑 데이터를 찾아서 상태변경
			ApprAgreementer agreementer = apprAgreementerRepository.findByMember_MemberNoAndApproval_ApprNo(member.getMember_no(), id);
			agreementer.setAgreementerAgreeStatus("R");
			agreementer.setAgreeReason(reason);
			ApprAgreementer entity = apprAgreementerRepository.save(agreementer);
			
			Approval approval = approvalRepository.findById(id).orElse(null);
			approval.setApprStatus("R");
			approval.setApprResDate(entity.getAgreementerAgreeStatusTime());
			approval.setApprReason(entity.getAgreeReason());
			approvalRepository.save(approval);
			
			result = 1;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}

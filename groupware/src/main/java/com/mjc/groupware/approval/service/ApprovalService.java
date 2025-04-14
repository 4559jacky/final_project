package com.mjc.groupware.approval.service;

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
import com.mjc.groupware.approval.repository.ApprAgreementerRepository;
import com.mjc.groupware.approval.repository.ApprApproverRepository;
import com.mjc.groupware.approval.repository.ApprReferencerRepository;
import com.mjc.groupware.approval.repository.ApprovalFormRepository;
import com.mjc.groupware.approval.repository.ApprovalRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ApprovalService {

	private final ApprovalFormRepository approvalFormRepository;
	private final ApprovalRepository approvalRepository;
	private final ApprApproverRepository apprApproverRepository;
	private final ApprAgreementerRepository apprAgreementerRepository;
	private final ApprReferencerRepository apprReferencerRepository;

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
			
			if(approvalDto.getAgreementer_no() == null) { 
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
			approverDto.setApprover_no(approvalDto.getApprover_no());
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
			if(approvalDto.getAgreementer_no() == null) {
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
			if(approvalDto.getReferencer_no() == null) {
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
	
}

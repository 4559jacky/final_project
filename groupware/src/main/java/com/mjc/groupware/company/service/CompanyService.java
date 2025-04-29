package com.mjc.groupware.company.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.company.dto.CompanyDto;
import com.mjc.groupware.company.dto.CompanyRuleDto;
import com.mjc.groupware.company.dto.FuncDto;
import com.mjc.groupware.company.entity.Company;
import com.mjc.groupware.company.entity.Func;
import com.mjc.groupware.company.repository.CompanyRepository;
import com.mjc.groupware.company.repository.FuncRepository;
import com.mjc.groupware.company.specification.FuncSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	
	@Value("${ffupload.location}")
	private String fileDir;
	
	private Logger logger = LoggerFactory.getLogger(CompanyService.class);
	
	private final CompanyRepository repository;
	private final FuncRepository funcRepository;
	
	public CompanyDto uploadFile(MultipartFile file) throws Exception {
		CompanyDto dto = new CompanyDto();
		
		try {
			if(file == null || file.isEmpty()) {
				dto.setOri_name(null);
				dto.setNew_name(null);
				dto.setAttach_path(null);
			} else {
				long fileSize = file.getSize();
				if(fileSize <= 0 || 2 * 1024 * 1024 < fileSize) {
					throw new Exception("파일 크기 : " + fileSize +"(파일 사이즈가 2MB를 넘습니다.");
				}
				
				String oriName = file.getOriginalFilename();
				dto.setOri_name(oriName);
				
				String fileExt = oriName.substring(oriName.lastIndexOf("."), oriName.length());
				
				String newName = UUID.randomUUID().toString().replaceAll("-", "") + fileExt;
				dto.setNew_name(newName);
				
				String downDir = fileDir+newName;
				dto.setAttach_path(downDir);
				
				File saveFile = new File(downDir);
				
				if(!saveFile.exists()) {
					saveFile.mkdirs();
				}
				
				file.transferTo(saveFile);
			}
		} 
		catch(Exception e) {
			dto = null;
			throw new Exception(e.getMessage());
		}
		
		return dto;
	}
	
	public void deleteFile(String filePath) throws Exception {
	    File file = new File(filePath);
	    
	    if (file.exists()) {
	        boolean deleted = file.delete();
	        if (!deleted) {
	            throw new Exception("파일 삭제 실패: " + filePath);
	        }
	    } else {
	        throw new Exception("파일이 존재하지 않습니다: " + filePath);
	    }
	}
	
	public void createCompany(CompanyDto dto) {
		try {
			Company param = Company.builder()
					.companyNo(dto.getCompany_no())
					.companyName(dto.getCompany_name())
					.oriName(dto.getOri_name())
					.newName(dto.getNew_name())
					.attachPath(dto.getAttach_path())
					.themeColor("Blue_Theme")
					.build();
			
			repository.save(param);
			
		} catch(Exception e) {
			logger.error("프로필 등록 중 오류 발생", e);
		}
	}
	
	public CompanyDto selectLatestCompanyProfile() {
		// 가장 마지막에 등록된 회사 프로필 이미지를 뽑아내는 로직
		Company latest = repository.findTop1ByOrderByRegDateDesc();
		
		if(latest == null) {
			return null;
		}
		
		return CompanyDto.builder()
				.company_no(latest.getCompanyNo())
				.company_name(latest.getCompanyName())
				.ori_name(latest.getOriName())
				.new_name(latest.getNewName())
				.attach_path(latest.getAttachPath())
				.theme_color(latest.getThemeColor())
				.company_initial(latest.getCompanyInitial())
				.rule_status(latest.getRuleStatus())
				.reg_date(latest.getRegDate())
				.mod_date(latest.getModDate())
				.light_logo_path("/uploads/" + latest.getNewName())
                .dark_logo_path("/uploads/" + latest.getNewName())
				.build();
	}
	
	@Transactional(rollbackFor = Exception.class)
	public CompanyDto updateThemeColor(String themeColor) {
		// 가장 마지막에 등록된 회사의 객체를 뽑아서 컬러를 수정해주는 로직
		Company latest = repository.findTop1ByOrderByRegDateDesc();
		
		if(latest == null) {
			return null;
		}
		
		latest.updateThemeColor(themeColor);
		
		return CompanyDto.builder()
				.company_no(latest.getCompanyNo())
				.company_name(latest.getCompanyName())
				.ori_name(latest.getOriName())
				.new_name(latest.getNewName())
				.attach_path(latest.getAttachPath())
				.theme_color(latest.getThemeColor())
				.build();
	}
	
	public List<FuncDto> selectPrimaryFuncAll() {
		List<Func> paramList = funcRepository.findAll(FuncSpecification.parentFuncIsNull());
		
		List<FuncDto> resultList = new ArrayList<>();
		
		for (Func func : paramList) {
	        FuncDto dto = FuncDto.builder()
	                .func_no(func.getFuncNo())
	                .func_name(func.getFuncName())
	                .func_code(func.getFuncCode())
	                .func_status(func.getFuncStatus())
	                .parent_func_no(func.getParentFunc() != null ? func.getParentFunc().getFuncNo() : null)
	                .reg_date(func.getRegDate())
	                .mod_date(func.getModDate())
	                .build();

	        resultList.add(dto);
	    }
		
		return resultList;
	}
	
	public void updateRule(CompanyRuleDto dto) {
		Company target = repository.findById(dto.getCompany_no()).orElseThrow(() -> new IllegalArgumentException("회사 정보가 존재하지 않습니다."));
		
		String companyInitial = dto.getCompany_initial();
		int ruleStatus;
		
	    try {
	        ruleStatus = Integer.parseInt(dto.getRule_status());
	    } catch (NumberFormatException e) {
	        throw new IllegalArgumentException("rule_status는 숫자만 입력 가능합니다. 유효하지 않은 값입니다.");
	    }

	    if (ruleStatus < 0 || ruleStatus > 1) {
	        throw new IllegalArgumentException("rule_status는 0 또는 1만 가능합니다.");
	    }
		
		target.changeCompanyInitial(companyInitial);
	    target.changeRuleStatus(ruleStatus);
		
	    repository.save(target);
		
	}
	
}

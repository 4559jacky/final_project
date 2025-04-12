package com.mjc.groupware.company.service;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.company.dto.CompanyDto;
import com.mjc.groupware.company.entity.Company;
import com.mjc.groupware.company.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	
	@Value("${ffupload.location}")
	private String fileDir;
	
	private Logger logger = LoggerFactory.getLogger(CompanyService.class);
	
	private final CompanyRepository repository;
	
	public CompanyDto uploadFile(MultipartFile file) {
		CompanyDto dto = new CompanyDto();
		
		try {
			if(file == null || file.isEmpty()) {
				throw new Exception("존재하지 않는 파일입니다.");
			}
			
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
		} catch(Exception e) {
			dto = null;
			logger.error("프로필 등록 중 오류 발생", e);
		}
		
		return dto;
	}
	
	public void createCompany(CompanyDto dto) {
		try {
			Company param = Company.builder()
					.companyNo(dto.getCompany_no())
					.companyName(dto.getCompany_name())
					.oriName(dto.getOri_name())
					.newName(dto.getNew_name())
					.attachPath(dto.getAttach_path())
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
				.reg_date(latest.getRegDate())
				.mod_date(latest.getModDate())
				.light_logo_path("/uploads/" + latest.getNewName())
                .dark_logo_path("/uploads/" + latest.getNewName())
				.build();
	}
}

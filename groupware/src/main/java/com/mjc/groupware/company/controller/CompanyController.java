package com.mjc.groupware.company.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.company.dto.CompanyDto;
import com.mjc.groupware.company.entity.Company;
import com.mjc.groupware.company.repository.CompanyRepository;
import com.mjc.groupware.company.service.CompanyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CompanyController {
	
	private Logger logger = LoggerFactory.getLogger(CompanyController.class);
	
	private final CompanyService service;
	private final CompanyRepository repository;
	
	@GetMapping("/admin/company/create")
	public String createCompanyView(Model model) {
		Company latest = repository.findTop1ByOrderByRegDateDesc();
		
		if(latest != null) {
			// 만약 이전에 등록했던 파일을 불러와서 미리보기로 보여주고싶다면? DB에 해당 정보를 저장하고 DTO에 같이 담아줘야함.
			CompanyDto companyDto = CompanyDto.builder()
					.company_no(latest.getCompanyNo())
					.company_name(latest.getCompanyName())
					.ori_name(latest.getOriName())
					.new_name(latest.getNewName())
					.attach_path(latest.getAttachPath())
					.build();
			
			if (companyDto.getProfile_image_path() != null) {
	            companyDto.setProfile_image_path(companyDto.getProfile_image_path());
	        } else {
	            companyDto.setProfile_image_path("/assets/images/big/img3.jpg");
	        }
			
			model.addAttribute("companyDto", companyDto);
		}
		
		return "/company/create";
	}
	
	@PostMapping("/admin/company/create")
	@ResponseBody
	public Map<String, String> createCompanyApi(CompanyDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		logger.info("CompanyDto: {}", dto);
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "프로필 정보 수정 중 알 수 없는 오류가 발생하였습니다");
		
		try {
			MultipartFile file = dto.getProfile_image();
			CompanyDto param = new CompanyDto();
			
			if (file == null || file.isEmpty()) {
		    	throw new IllegalArgumentException("이미지 파일을 등록해주세요");
		    } else {
		    	CompanyDto uploadedDto = service.uploadFile(file);
		    	param.setOri_name(uploadedDto.getOri_name());
		    	param.setNew_name(uploadedDto.getNew_name());
		    	param.setAttach_path(uploadedDto.getAttach_path());
		    	param.setProfile_image_path(uploadedDto.getAttach_path());
		    }
			
			param.setCompany_no(dto.getCompany_no());
	        param.setCompany_name(dto.getCompany_name());
			
			service.createCompany(param);
			
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "프로필 정보가 수정되었습니다");
		} catch(IllegalArgumentException e) {
			logger.error("프로필 등록 중 오류 발생", e);
			resultMap.put("res_code", "400");
	        resultMap.put("res_msg", e.getMessage());
		} catch(Exception e) {
			logger.error("프로필 등록 중 오류 발생", e);
			resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "프로필 정보 수정 중 알 수 없는 오류가 발생했습니다.");
		}

		return resultMap;
	}
	
}

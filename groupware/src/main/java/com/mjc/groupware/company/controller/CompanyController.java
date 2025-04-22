package com.mjc.groupware.company.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.company.dto.CompanyDto;
import com.mjc.groupware.company.dto.FuncDetailResponseDto;
import com.mjc.groupware.company.dto.FuncDto;
import com.mjc.groupware.company.dto.FuncMappingRequestDto;
import com.mjc.groupware.company.entity.Company;
import com.mjc.groupware.company.repository.CompanyRepository;
import com.mjc.groupware.company.service.CompanyService;
import com.mjc.groupware.company.service.FuncService;
import com.mjc.groupware.member.dto.RoleDto;
import com.mjc.groupware.member.entity.Role;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.member.service.RoleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CompanyController {
	
	private Logger logger = LoggerFactory.getLogger(CompanyController.class);
	
	private final CompanyService service;
	private final CompanyRepository repository;
	private final FuncService funcService;
	private final RoleService roleService;
	private final MemberRepository memberRepository;
	
	@GetMapping("/admin/company")
	public String companySettingsView(Model model) {
		// 이미지 보내주기
		Company latest = repository.findTop1ByOrderByRegDateDesc();
		
		if(latest != null) {
			// 만약 이전에 등록했던 파일을 불러와서 미리보기로 보여주고싶다면? DB에 해당 정보를 저장하고 DTO에 같이 담아줘야함.
			CompanyDto companyDto = CompanyDto.builder()
					.company_no(latest.getCompanyNo())
					.company_name(latest.getCompanyName())
					.ori_name(latest.getOriName())
					.new_name(latest.getNewName())
					.attach_path(latest.getAttachPath())
					.theme_color(latest.getThemeColor())
					.build();
			
			if (companyDto.getProfile_image_path() != null) {
	            companyDto.setProfile_image_path(companyDto.getProfile_image_path());
	        } else {
	            companyDto.setProfile_image_path("/assets/images/big/img3.jpg");
	        }
			
			model.addAttribute("companyDto", companyDto);
		}
		
		// 기능 리스트 보내주기
		List<FuncDto> funcList = service.selectPrimaryFuncAll();
		
		if(funcList != null) {
			model.addAttribute("funcList", funcList);			
		}
		
		// 역할 리스트 보내주기
		List<Role> roleParam = roleService.selectRoleAll();
		
		List<RoleDto> roleList = new ArrayList<>();
				
		for(Role role : roleParam) {
			int count = memberRepository.countByRole(role);
			
			RoleDto dto = RoleDto.builder()
					.role_no(role.getRoleNo())
					.role_name(role.getRoleName())
					.role_nickname(role.getRoleNickname())
					.member_count(count)
					.build();
			
			roleList.add(dto);
		}
		
		if(roleList != null && !roleList.isEmpty()) {
			model.addAttribute("roleList", roleList);
		}
		
		return "/company/settings";
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
			resultMap.put("res_msg", "프로필 정보 수정이 정상적으로 완료되었습니다.");
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
	
	@PostMapping("/admin/company/color/update")
	@ResponseBody
	public Map<String, String> updateThemeColor(@RequestBody Map<String, String> requestData) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "테마 색상 변경 중 알 수 없는 오류가 발생했습니다.");
		
		try {
			String themeColor = requestData.get("theme_color");
			
			CompanyDto result = service.updateThemeColor(themeColor);
			resultMap.put("res_theme_color", result.getTheme_color());
			
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "테마 색상 변경이 정상적으로 완료되었습니다.");
		} catch(Exception e) {
			logger.error("테마 색상 변경 중 오류 발생", e);
			resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "테마 색상 변경 중 알 수 없는 오류가 발생했습니다.");
		}
		
		return resultMap;
	}
	
	@PostMapping("/admin/company/func/update")
	@ResponseBody
	public Map<String, String> updateAvailableFuncApi(@RequestBody FuncDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
        resultMap.put("res_msg", "사용할 기능 변경 중 알 수 없는 오류가 발생했습니다.");
		
        logger.info("FuncDto: {}", dto);
        
		try {
			funcService.updateAvailableFunc(dto);
			
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "사용할 기능 변경이 정상적으로 완료되었습니다.");
		} catch(IllegalArgumentException e) {
			logger.error("사용할 기능 변경 중 오류 발생", e);
			resultMap.put("res_code", "400");
	        resultMap.put("res_msg", e.getMessage());
		
		} catch(Exception e) {
			resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "사용할 기능 변경 중 알 수 없는 오류가 발생했습니다.");
		}
		
		return resultMap;
	}
	
	@GetMapping("/admin/company/func/{id}/detail")
	@ResponseBody
	public FuncDetailResponseDto selectSubFuncView(@PathVariable("id") Long funcNo) {
        logger.info("funcNo: {}", funcNo);
		
		try {
			FuncDto func = funcService.selectFuncByFuncNoWithRoles(funcNo);
			List<FuncDto> children = funcService.selectSubFuncByFuncNoWithRoles(funcNo);
			
			List<RoleDto> roleList = roleService.selectRoleDtoAll();
			
			return FuncDetailResponseDto.builder()
					.res_code("200")
					.res_msg("기능 조회가 정상적으로 완료되었습니다.")
					.funcDto(func)
					.funcDtoList(children)
					.roleDtoList(roleList)
					.build();
			
		} catch(Exception e) {
			logger.error("하위 기능 조회 중 오류 발생", e);
			return FuncDetailResponseDto.builder()
					.res_code("500")
					.res_msg("기능 조회 중 알 수 없는 오류가 발생했습니다.")
					.funcDto(null)
					.funcDtoList(Collections.emptyList())
					.roleDtoList(Collections.emptyList())
					.build();
		}
	}
	
	@PostMapping("/admin/company/funcMapping/update")
	@ResponseBody
	public Map<String, String> updateFuncMapping(@RequestBody FuncMappingRequestDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
        resultMap.put("res_msg", "접근 권한 설정 중 알 수 없는 오류가 발생했습니다.");
		
		logger.info("FuncMappingRequestDto: {}", dto);
		
		try {
			funcService.updateFuncMapping(dto);
			
			resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "접근 권한 설정이 성공적으로 반영되었습니다.");
		} catch(Exception e) {
			logger.error("접근 권한 설정 중 예외 발생", e);
			resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "접근 권한 설정 중 알 수 없는 오류가 발생했습니다.");
		}
		
		return resultMap;
	}
	
}

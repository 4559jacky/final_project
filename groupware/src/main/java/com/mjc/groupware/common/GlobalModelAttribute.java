package com.mjc.groupware.common;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.mjc.groupware.company.dto.CompanyDto;
import com.mjc.groupware.company.service.CompanyService;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttribute {
	
	private final CompanyService companyService;
	
	@ModelAttribute("theme")
    public String theme(@CookieValue(value = "theme", defaultValue = "light") String theme) {
		// 모든 페이지 전역으로 쿠키를 뿌리는 로직 - 지금 쿠키에 어떤 테마라고 저장되어있는지 프론트단에 뿌려줌 - 즉, 동적으로 thymeleaf 쓸 수 있게 됨
        return theme;
    }
	
	@ModelAttribute
	public void selectLatestCompanyProfile(Model model) {
		// 모든 페이지에 전역으로 마지막에 등록된 companyDto를 뿌리는 로직
		// 페이지가 로드될 때마다 service를 찾아가서 repository를 거쳐서 엔티티를 조회한 후 디티오로 바꿔서 가져온 다음 프론트에 뿌림
	    CompanyDto latestProfile = companyService.selectLatestCompanyProfile();
	    String latestProfileName = latestProfile.getCompany_name();
	    model.addAttribute("latestProfile", latestProfile);
	    model.addAttribute("latestProfileName", latestProfileName);
	}
	
}

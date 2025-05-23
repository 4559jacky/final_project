package com.mjc.groupware.company.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="company")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Company {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="company_no")
	private Long companyNo;
	
	@Column(name="company_name")
	private String companyName;
	
	@Column(name="ori_name")
	private String oriName;
	
	@Column(name="new_name")
	private String newName;
	
	@Column(name="attach_path")
	private String attachPath;
	
	@Column(name="theme_color")
	private String themeColor;
	
	@Column(name="company_initial")
	private String companyInitial;
	
	@Column(name="rule_status")
	private int ruleStatus;
	
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
	private LocalDateTime regDate;
	
	@UpdateTimestamp
	@Column(insertable=false,name="mod_date")
	private LocalDateTime modDate;
	
	// 테마 색상을 변경하는 도메인 메소드
	public void updateThemeColor(String themeColor) {
		this.themeColor = themeColor;
	}
	
	// 회사 이니셜을 변경하는 도메인 메소드
    public void changeCompanyInitial(String companyInitial) {
        this.companyInitial = companyInitial;
    }

    // 규칙 상태를 변경하는 도메인 메소드
    public void changeRuleStatus(int ruleStatus) {
        this.ruleStatus = ruleStatus;
    }
	
}

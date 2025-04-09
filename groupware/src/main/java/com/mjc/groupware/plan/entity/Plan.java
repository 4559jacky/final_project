package com.mjc.groupware.plan.entity;

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
import lombok.Setter;

@Entity
@Table(name="plan")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Plan {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_no")
    private Long planNo;

	@Column(name = "plan_name")
	private String planName;
	
	@Column(name = "plan_content")
    private String planContent;
	
	@Column(name = "start_date")
    private String startDate;
	
	@Column(name = "end_date")
    private String endDate;
	
	@Column(name = "color")
    private String color;
	
	@Column(name = "reg_member_no")
    private int regMemberNo;
	
	@Column(name = "repeat_yn")
    private String repeatYn;
	
	@Column(name = "delete_yn")
    private String deleteYn;
	
	@Column(name = "allday_yn")
    private String alldayYn;
	
	@Column(name = "plan_type")
    private String planType;

}

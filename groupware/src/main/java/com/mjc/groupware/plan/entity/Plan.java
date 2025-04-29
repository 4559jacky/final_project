package com.mjc.groupware.plan.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="plan")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Plan {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_no")
    private Long planNo;

	@Column(name = "plan_title")
	private String planTitle;
	
	@Column(name = "plan_content")
    private String planContent;
	
	@Column(name = "color")
	private String color;
	
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
    private LocalDateTime regDate;
	
	@UpdateTimestamp
	@Column(insertable=false,name="mod_date")
    private LocalDateTime modDate;
	
	@Column(name = "start_date")
    private LocalDateTime startDate;
	
	@Column(name = "end_date")
    private LocalDateTime endDate;
	
	@ManyToOne
	@JoinColumn(name = "reg_member_no")
    private Member member;
	
	@Column(name = "plan_type")
    private String planType;

	@Column(name = "last_update_member")
	private Long lastUpdateMember;

	@Column(name = "del_yn")
	private String delYn;
	
}

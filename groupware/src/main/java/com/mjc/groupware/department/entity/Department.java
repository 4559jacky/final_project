package com.mjc.groupware.department.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="department")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Department {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="dept_no")
	private Long deptNo;
	
	@Column(name="dept_name")
	private String deptName;
	
	@Column(name="parent_dept_no")
	private Long parentDeptNo;
	
	@Column(name="dept_phone")
	private String deptPhone;
	
	@Column(name="dept_location")
	private String deptLocation;
	
	public enum Status {
        INACTIVE,
        ACTIVE
    }
	
    @Enumerated(EnumType.STRING)
    @Column(name = "dept_status")
    private Status deptStatus;
	
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
	private LocalDateTime regDate;
	
	@UpdateTimestamp
	@Column(insertable=false,name="mod_date")
	private LocalDateTime modDate;
	
	@ManyToOne
	@JoinColumn(name="member_no")
	private Member member;
	
	@OneToMany(mappedBy="department")
	private List<Member> members;
	
}

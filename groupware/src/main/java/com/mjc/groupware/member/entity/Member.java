package com.mjc.groupware.member.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.department.entity.Department;
import com.mjc.groupware.position.entity.Position;

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
@Table(name="member")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Member {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="member_no")
	public Long memberNo;
	
	@Column(name="member_id")
	public String memberId;
	
	@Column(name="member_pw")
	public String memberPw;
	
	@Column(name="member_name")
	private String memberName;
	
	@Column(name="member_birth")
	private String memberBirth;
	
	@Column(name="member_gender")
	private String memberGender;
	
	@Column(name="member_addr")
	private String memberAddr;
	
	@Column(name="member_phone")
	private String memberPhone;
	
	public enum Status {
        EMPLOYED,
        RESIGNED,
        ON_LEAVE
    }
	
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
	
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
	private LocalDateTime regDate;
	
	@UpdateTimestamp
	@Column(insertable=false,name="mod_date")
	private LocalDateTime modDate;
	
	@Column(name = "end_date")
	private LocalDateTime endDate;
	
	@ManyToOne
	@JoinColumn(name="dept_no")
	private Department department;
	
	@ManyToOne
	@JoinColumn(name="pos_no")
	private Position position;
	
	@ManyToOne
	@JoinColumn(name="role_no")
	private Role role;
	
	@OneToMany(mappedBy="member")
	private List<Department> departments;
	
}

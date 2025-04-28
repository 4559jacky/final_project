package com.mjc.groupware.dept.entity;

import java.time.LocalDateTime;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="dept")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Dept {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="dept_no")
	private Long deptNo;
	
	@Column(name="dept_name", nullable = false)
	private String deptName;
	
	@ManyToOne
	@JoinColumn(name="parent_dept_no")
	private Dept parentDept;
	
	@Column(name="dept_phone")
	private String deptPhone;
	
	@Column(name="dept_location")
	private String deptLocation;
	
    @Column(name = "dept_status")
    private int deptStatus;
	
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
	private LocalDateTime regDate;
	
	@UpdateTimestamp
	@Column(insertable=false,name="mod_date")
	private LocalDateTime modDate;
	
	@ManyToOne
	@JoinColumn(name="member_no")
	private Member member;
	
	@OneToMany(mappedBy="dept")
	private List<Member> members;
	
	public void clearDeptHead() {
        if (this.member != null) {
            this.member = null;
        }
    }
	
	public void changeDeptManager(Member newManager) {
        if (this.member != null && this.member.equals(newManager)) {
            return;
        }
        this.member = newManager;
    }
}

package com.mjc.groupware.shared.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.pos.entity.Pos;

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

@Entity
@Table(name="shared_folder")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SharedFolder {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="folder_no")
	private Long folderNo;
	
	@Column(name="folder_name")
	private String folderName;
	
	@Column(name="folder_status")
	private String folderStatus;
	
	@Column(name="folder_shared")
	private String folderShared;
	
	@ManyToOne
	@JoinColumn(name="member_no", nullable = false)
	private Member member;
	
	@ManyToOne
	@JoinColumn(name="folder_parent_no")
	private SharedFolder parentFolder;
	
	@ManyToOne
	@JoinColumn(name="dept_no")
    private Dept dept;
	
	@ManyToOne
	@JoinColumn(name="pos_no")
    private Pos pos;
	
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
	private LocalDateTime regDate;
}

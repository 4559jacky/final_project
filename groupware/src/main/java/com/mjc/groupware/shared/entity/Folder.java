package com.mjc.groupware.shared.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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

@Entity
@Table(name="shared_folder")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Folder {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="folder_no")
	private Long folderNo;
	
	@Column(name="folder_name")
	private String folderName;
	
	@ManyToOne
	@JoinColumn(name="member_no", nullable = false)
	private Member member;
	
	@ManyToOne
	@JoinColumn(name="folder_parent_no")
	private Folder parentFolder;
	
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
	private LocalDateTime regDate;
}

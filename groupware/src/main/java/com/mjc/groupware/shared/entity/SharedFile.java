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
import lombok.Setter;

@Entity
@Table(name="shared_file")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SharedFile {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="file_no")
	private Long fileNo;
	
	@Column(name="file_name")
	private String fileName;
	
	@Column(name="file_size")
	private Long fileSize;
	
	@Column(name="file_path")
	private String filePath;
	
	@Column(name="file_shared")
	private String fileShared;
	
	@Column(name="file_status")
	private String fileStatus;
	
	@ManyToOne
	@JoinColumn(name="member_no", nullable = false)
	private Member member;
	
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
	private LocalDateTime regDate;
	
	@Column(name="file_deleted_by")
	private Long fileDeletedBy;

	@Column(name="file_deleted_at")
	private LocalDateTime fileDeletedAt;

	@Column(name="original_folder_no")
	private Long originalFolderNo;
	
	@ManyToOne
	@JoinColumn(name="folder_no", nullable = false)
	private SharedFolder folder;
}

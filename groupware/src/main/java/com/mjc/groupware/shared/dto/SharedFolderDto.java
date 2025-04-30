package com.mjc.groupware.shared.dto;


import java.time.LocalDateTime;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.shared.entity.SharedFolder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SharedFolderDto {
	private Long folder_no;
	private String folder_name;
	private String folder_status;
	private Integer folder_type;
	private Long folder_parent_no;
	private Long dept_no;
	private Long pos_no;
	private Long member_no;
	private LocalDateTime reg_date;

	// 📌 삭제자 정보
	private Long folder_deleted_by;          // 삭제한 사용자 번호
	private LocalDateTime folder_deleted_at; // 삭제 일시

	public SharedFolder toEntity(){
		return SharedFolder.builder()
				.folderNo(folder_no)
				.folderName(folder_name)
				.folderStatus(folder_status)
				.folderType(folder_type)
				.member(Member.builder().memberNo(member_no).build())
				.parentFolder(folder_parent_no != null ? SharedFolder.builder().folderNo(folder_parent_no).build() : null)
				// 🔽 새 필드
				.folderDeletedBy(folder_deleted_by)
				.folderDeletedAt(folder_deleted_at)
				.build();
	}

	public SharedFolderDto toDto(SharedFolder folder) {
		return SharedFolderDto.builder()
				.folder_no(folder.getFolderNo())
				.folder_name(folder.getFolderName())
				.folder_status(folder.getFolderStatus())
				.folder_type(folder.getFolderType())
				.folder_parent_no(folder.getParentFolder() != null ? folder.getParentFolder().getFolderNo() : null)
				.member_no(folder.getMember() != null ? folder.getMember().getMemberNo() : null)
				.reg_date(folder.getRegDate())
				// 🔽 새 필드
				.folder_deleted_by(folder.getFolderDeletedBy())
				.folder_deleted_at(folder.getFolderDeletedAt())
				.build();
	}
}

package com.mjc.groupware.shared.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.pos.entity.Pos;
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
	private String folder_shared;
	private Long folder_parent_no;
	private Long dept_no;
	private Long pos_no;
	private Long member_no;
	private LocalDateTime reg_date;
	
	
	public SharedFolder toEntity(){
		return SharedFolder.builder()
				.folderNo(folder_no)
				.folderName(folder_name)
				/*
				 * .dept(Dept.builder().deptNo(dept_no).build())
				 * .pos(Pos.builder().posNo(pos_no).build())
				 */
				.member(Member.builder().memberNo(member_no).build())
				.parentFolder(folder_parent_no != null ? SharedFolder.builder().folderNo(folder_parent_no).build() : null)
				.folderStatus(folder_status)
				.folderShared(folder_shared)
				.build();
	}
	
	public SharedFolderDto toDto(SharedFolder folder) {
		return SharedFolderDto.builder()
				.folder_no(folder.getFolderNo())
				.folder_name(folder.getFolderName())
				.folder_parent_no(folder.getParentFolder() != null ? folder.getParentFolder().getFolderNo() : null)
				.member_no(folder.getMember() != null ? folder.getMember().getMemberNo() : null)
				/*
				 * .pos_no(folder.getPos() != null ? folder.getPos().getPosNo() : null)
				 * .dept_no(folder.getDept() != null ? folder.getDept().getDeptNo() : null)
				 */
				.reg_date(folder.getRegDate())
				.folder_shared(folder.getFolderShared())
				.folder_status(folder.getFolderStatus())
				.build();
	}
}
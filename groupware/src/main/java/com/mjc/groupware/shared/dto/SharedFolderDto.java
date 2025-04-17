package com.mjc.groupware.shared.dto;

import java.time.LocalDateTime;

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
	private Long folder_parent_no;
	private Long member_no;
	private LocalDateTime reg_date;
	
	
	public SharedFolder toEntity(){
		return SharedFolder.builder()
				.folderNo(folder_no)
				.folderName(folder_name)
				.build();
	}
	
	public SharedFolderDto toDto(SharedFolder folder) {
		return SharedFolderDto.builder()
				.folder_no(folder.getFolderNo())
				.folder_name(folder.getFolderName())
				.build();
	}
}
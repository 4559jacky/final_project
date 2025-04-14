package com.mjc.groupware.shared.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.shared.entity.Folder;

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
public class FolderDto {
	private Long folder_no;
	private String folder_name;
	private Long folder_parent_no;
	private Long member_no;
	private LocalDateTime reg_date;
	
	
	public Folder toEntity(){
		return Folder.builder()
				.folderNo(folder_no)
				.folderName(folder_name)
				.build();
	}
	
	public FolderDto toDto(Folder folder) {
		return FolderDto.builder()
				.folder_no(folder.getFolderNo())
				.folder_name(folder.getFolderName())
				.build();
	}
}
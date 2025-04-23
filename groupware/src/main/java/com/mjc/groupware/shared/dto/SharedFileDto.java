package com.mjc.groupware.shared.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.shared.entity.SharedFile;

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
public class SharedFileDto {
	private Long file_no;
	private String file_name;
	private String file_new_name;
	private String file_path;
	private char file_status;
	//공유 여부
	private char file_shared;
	private Long folder_no;
	private Long member_no;
	private LocalDateTime reg_date;

	public SharedFile toEntity() {
		return SharedFile.builder()
				.fileNo(file_no)
				.fileName(file_name)
				.fileNewName(file_new_name)
				.filePath(file_path)
				.fileShared(file_shared)
				.fileStatus(file_status)
				.build();
	}
	
	public SharedFileDto toDto(SharedFile file) {
		return SharedFileDto.builder()
				.file_no(file.getFileNo())
				.file_name(file.getFileName())
				.file_new_name(file.getFileNewName())
				.file_path(file.getFilePath())
				.file_shared(file.getFileShared())
				.file_status(file.getFileStatus())
				.build();
	}
}

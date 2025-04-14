package com.mjc.groupware.shared.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.shared.entity.File;

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
public class FileDto {
	private Long file_no;
	private String file_name;
	private String file_new_name;
	private String file_path;
	//공유 여부
	private char file_shared;
	private Long folder_no;
	private Long member_no;
	private LocalDateTime reg_date;

	public File toEntity() {
		return File.builder()
				.fileNo(file_no)
				.fileName(file_name)
				.fileNewName(file_new_name)
				.filePath(file_path)
				.fileShared(file_shared)
				.build();
	}
	
	public FileDto toDto(File file) {
		return FileDto.builder()
				.file_no(file.getFileNo())
				.file_name(file.getFileName())
				.file_new_name(file.getFileNewName())
				.file_path(file.getFilePath())
				.file_shared(file.getFileShared())
				.build();
	}
}

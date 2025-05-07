// SharedRestoreRequestDto.java
package com.mjc.groupware.shared.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SharedRestoreRequestDto {
    private List<Long> folderIds;
    private List<Long> fileIds;
}
package com.mjc.groupware.accommodationReservation.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.accommodationReservation.entity.AccommodationAttach;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;

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
public class AccommodationAttachDto {
	
	private Long attach_no;
	private String ori_name;
	private String new_name;
	private String attach_path;
	private Long accommodation_no;
	
	public AccommodationAttach toEntity() {
        return AccommodationAttach.builder()
                .attachNo(attach_no)
                .oriName(ori_name)
                .newName(new_name)
                .attachPath(attach_path)
                .accommodationInfo(AccommodationInfo.builder().accommodationNo(accommodation_no).build())
                .build();
    }
	
}

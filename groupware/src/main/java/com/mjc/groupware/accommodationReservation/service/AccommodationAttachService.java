package com.mjc.groupware.accommodationReservation.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.accommodationReservation.dto.AccommodationAttachDto;
import com.mjc.groupware.accommodationReservation.entity.AccommodationAttach;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;
import com.mjc.groupware.accommodationReservation.repository.AccommodationAttachRepository;
import com.mjc.groupware.accommodationReservation.repository.AccommodationInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationAttachService {

	@Value("${ffupload.location}")
	private String fileDir;
	
	private final AccommodationAttachRepository accommodationAttachRepository;
    private final AccommodationInfoRepository accommodationInfoRepository;
    
	public AccommodationAttachDto uploadFile(MultipartFile file) {
		AccommodationAttachDto dto = new AccommodationAttachDto();
		try {
			if(file == null || file.isEmpty()) {
				throw new Exception("존재하지 않는 파일입니다.");
			}
			long fileSize = file.getSize();
			if(fileSize >= 1048576) {
				throw new Exception("허용 용량을 초과한 파일입니다.");
			}
			// 파일 최초이름 읽어오기
			String oriName = file.getOriginalFilename();
			dto.setOri_name(oriName);
			// 파일 확장자 자르기
			String fileExt = oriName.substring(oriName.lastIndexOf("."),oriName.length());
			// 파일명칭 바꾸기
			UUID uuid = UUID.randomUUID();
			// uuid의 8자리마다 반복되는 하이픈 제거
			String uniqueName = uuid.toString().replaceAll("-", "");
			// 새로운 파일명 생성
			String newName = uniqueName+fileExt;
			dto.setNew_name(newName);
			// 파일 저장 경로 설정
			String downDir = fileDir+"accommodation/"+newName;
			dto.setAttach_path(downDir);
			// 파일 껍데기 생성
			File dir = new File(fileDir + "accommodation/");
			if (!dir.exists()) {
			    dir.mkdirs(); // 디렉토리 생성
			}
			// 껍데기에 파일 정보 복제
			File saveFile = new File(dir, newName);
			file.transferTo(saveFile);
			
		}catch(Exception e) {
			dto = null;
			e.printStackTrace();
		}
		return dto;
	}
	
	public void saveAttach(AccommodationAttachDto dto, AccommodationInfo accommodationInfo) {
	    AccommodationAttach attach = AccommodationAttach.builder()
	        .oriName(dto.getOri_name())
	        .newName(dto.getNew_name())
	        .attachPath(dto.getAttach_path())
	        .accommodationInfo(accommodationInfo)
//	        .regDate(LocalDateTime.now())
	        .build();

	    accommodationAttachRepository.save(attach);
	}

	// 이미지 수정시 기존 파일 삭제
	public void deleteAllByAccommodation(Long accommodationNo) {
    	List<AccommodationAttach> attachList = accommodationAttachRepository.findByAccommodationInfo_AccommodationNo(accommodationNo);
        accommodationAttachRepository.deleteAll(attachList);
    }

	
}

package com.mjc.groupware.accommodationReservation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mjc.groupware.accommodationReservation.dto.AccommodationAttachDto;
import com.mjc.groupware.accommodationReservation.dto.AccommodationInfoDto;
import com.mjc.groupware.accommodationReservation.entity.AccommodationAttach;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;
import com.mjc.groupware.accommodationReservation.repository.AccommodationAttachRepository;
import com.mjc.groupware.accommodationReservation.repository.AccommodationInfoRepository;
import com.mjc.groupware.board.entity.Board;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationService {
	
	private final AccommodationInfoRepository accommodationInfoRepository;
	private final AccommodationAttachRepository accommodationAttachRepository;
	
	// 숙소등록
	public AccommodationInfo register(AccommodationInfoDto dto) {
		dto.setAccommodation_no(null); // <-- 🔥 명시적 null 처리
		AccommodationInfo entity = dto.toEntity();
		return accommodationInfoRepository.save(entity);
	}
    
    // 목록출력
    public List<AccommodationInfo> getAllAccommodations() {
    	List<AccommodationInfo> list = accommodationInfoRepository.findAll();
        System.out.println("조회된 숙소 수: " + list.size());
        for (AccommodationInfo info : list) {
            System.out.println(info.getAccommodationName());
        }
        return list;
    }
    
    // 숙소 상세
    public AccommodationInfoDto findById(Long accommodationNo) {
        AccommodationInfo accom = accommodationInfoRepository.findById(accommodationNo)
            .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));
        
        AccommodationInfoDto dto = new AccommodationInfoDto();
        dto.setAccommodation_no(accom.getAccommodationNo());
        dto.setAccommodation_name(accom.getAccommodationName());
        dto.setAccommodation_type(accom.getAccommodationType());
        dto.setAccommodation_address(accom.getAccommodationAddress());
        dto.setAccommodation_phone(accom.getAccommodationPhone());
        dto.setAccommodation_email(accom.getAccommodationEmail());
        dto.setAccommodation_site(accom.getAccommodationSite());
        dto.setAccommodation_location(accom.getAccommodationLocation());
        dto.setRoom_count(accom.getRoomCount());
        dto.setAccommodation_content(accom.getAccommodationContent());
        dto.setRoom_price(accom.getRoomPrice());
        dto.setReg_date(accom.getRegDate());
        dto.setMod_date(accom.getModDate());

     // 첨부파일 리스트 조회 및 DTO 변환
        List<AccommodationAttach> attachList = accommodationAttachRepository
                .findByAccommodationInfo_AccommodationNo(accommodationNo);
        List<AccommodationAttachDto> attachDtoList = new ArrayList<>();
        
        for (AccommodationAttach attach : attachList) {
            AccommodationAttachDto attachDto = new AccommodationAttachDto();
            attachDto.setAttach_no(attach.getAttachNo());
            attachDto.setOri_name(attach.getOriName());
            attachDto.setNew_name(attach.getNewName());
            attachDto.setAttach_path(attach.getAttachPath());
            attachDto.setAccommodation_no(accommodationNo);

            attachDtoList.add(attachDto);
        }

        dto.setAttachList(attachDtoList); // AccommodationInfoDto에 담기
        
        return dto;
    }

    public List<AccommodationAttachDto> findAttachList(Long accommodationNo) {
        List<AccommodationAttach> attachList = accommodationAttachRepository.findByAccommodationInfo_AccommodationNo(accommodationNo);
        List<AccommodationAttachDto> dtoList = new ArrayList<>();

        for (AccommodationAttach attach : attachList) {
            AccommodationAttachDto dto = new AccommodationAttachDto();
            dto.setAttach_no(attach.getAttachNo());
            dto.setOri_name(attach.getOriName());
            dto.setNew_name(attach.getNewName());
            dto.setAttach_path(attach.getAttachPath());
            dto.setAccommodation_no(attach.getAccommodationInfo().getAccommodationNo());
            dtoList.add(dto);
        }

        return dtoList;
    }

    // 수정
    public AccommodationInfo update(AccommodationInfoDto dto) {
    	AccommodationInfo entity = accommodationInfoRepository.findById(dto.getAccommodation_no())
    			.orElseThrow(() -> new IllegalArgumentException("수정할 숙소를 찾을 수 없습니다."));
    	
    	entity.setAccommodationName(dto.getAccommodation_name());
    	entity.setAccommodationType(dto.getAccommodation_type());
    	entity.setAccommodationAddress(dto.getAccommodation_address());
    	entity.setAccommodationPhone(dto.getAccommodation_phone());
    	entity.setAccommodationEmail(dto.getAccommodation_email());
    	entity.setAccommodationSite(dto.getAccommodation_site());
    	entity.setAccommodationLocation(dto.getAccommodation_location());
    	entity.setRoomCount(dto.getRoom_count());
    	entity.setAccommodationContent(dto.getAccommodation_content());
    	entity.setRoomPrice(dto.getRoom_price());
    	entity.setModDate(dto.getMod_date());
    	
    	return accommodationInfoRepository.save(entity); // 변경 후 저장
    }

    // 숙소 삭제
	public void deleteAccommodation(Long accommodationNo) {
		accommodationInfoRepository.deleteById(accommodationNo);
	}

 


}

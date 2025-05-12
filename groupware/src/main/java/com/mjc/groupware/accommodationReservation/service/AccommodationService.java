package com.mjc.groupware.accommodationReservation.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mjc.groupware.accommodationReservation.dto.AccommodationAttachDto;
import com.mjc.groupware.accommodationReservation.dto.AccommodationInfoDto;
import com.mjc.groupware.accommodationReservation.entity.AccommodationAttach;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;
import com.mjc.groupware.accommodationReservation.repository.AccommodationAttachRepository;
import com.mjc.groupware.accommodationReservation.repository.AccommodationInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationService {
	
	private final AccommodationInfoRepository accommodationInfoRepository;
	private final AccommodationAttachRepository accommodationAttachRepository;
	
	// 숙소등록(관리자)
	public AccommodationInfo register(AccommodationInfoDto dto) {
		dto.setAccommodation_no(null); 
		AccommodationInfo entity = dto.toEntity();
		return accommodationInfoRepository.save(entity);
	}
    
    // 목록출력(관리자,사용자)
	public List<AccommodationInfoDto> getAllAccommodations() {
	    List<AccommodationInfo> list = accommodationInfoRepository.findAll();
	    List<AccommodationInfoDto> dtoList = new ArrayList<>();

	    for (AccommodationInfo accom : list) {
	        AccommodationInfoDto dto = new AccommodationInfoDto();
	        dto.setAccommodation_no(accom.getAccommodationNo());
	        dto.setAccommodation_name(accom.getAccommodationName());
	        dto.setAccommodation_type(accom.getAccommodationType());
	        dto.setAccommodation_address(accom.getAccommodationAddress());
	        dto.setAccommodation_phone(accom.getAccommodationPhone());
	        dto.setAccommodation_email(accom.getAccommodationEmail());
	        dto.setAccommodation_site(accom.getAccommodationSite());
	        dto.setAccommodation_location(accom.getAccommodationLocation());
	        dto.setAccommodation_content(accom.getAccommodationContent());
	        dto.setRoom_price(accom.getRoomPrice());
	        dto.setReg_date(accom.getRegDate());
	        dto.setMod_date(accom.getModDate());

	        dtoList.add(dto);
	    }

	    return dtoList;
	}
	
	// 사용자 홈화면 목록용 (썸네일 이미지 포함)
//	public List<AccommodationInfoDto> showHomeView() {
//	    List<AccommodationInfo> list = accommodationInfoRepository.findAll();
//	    List<AccommodationInfoDto> dtoList = new ArrayList<>();
//
//	    for (AccommodationInfo accom : list) {
//	        AccommodationInfoDto dto = new AccommodationInfoDto();
//	        dto.setAccommodation_no(accom.getAccommodationNo());
//	        dto.setAccommodation_name(accom.getAccommodationName());
//	        dto.setRoom_price(accom.getRoomPrice());
//
//	        // 대표 이미지 1개만 추가
//	        List<AccommodationAttach> attachList = accommodationAttachRepository
//	                .findByAccommodationInfo_AccommodationNo(accom.getAccommodationNo());
//
//	        if (!attachList.isEmpty()) {
//	            AccommodationAttach attach = attachList.get(0); // 첫 번째 이미지
//	            AccommodationAttachDto attachDto = new AccommodationAttachDto();
//	            attachDto.setAttach_no(attach.getAttachNo());
//	            attachDto.setNew_name(attach.getNewName());
//	            attachDto.setAttach_path(attach.getAttachPath());
//	            attachDto.setAccommodation_no(accom.getAccommodationNo());
//
//	            dto.setAttachList(List.of(attachDto));
//	        }
//
//	        dtoList.add(dto);
//	    }
//
//	    return dtoList;
//	}

    // 숙소 상세(관리자)
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

    // 숙소 수정(관리자)
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
//    	entity.setRoomCount(dto.getRoom_count());
    	entity.setAccommodationContent(dto.getAccommodation_content());
    	entity.setRoomPrice(dto.getRoom_price());
    	entity.setModDate(dto.getMod_date());
    	
    	return accommodationInfoRepository.save(entity); // 변경 후 저장
    }

    // 숙소 삭제(관리자)
	public void deleteAccommodation(Long accommodationNo) {
		accommodationInfoRepository.deleteById(accommodationNo);
	}

	//필터링
	public List<AccommodationInfoDto> getFilteredList(String address, String sort, String type) {
	    List<AccommodationInfo> list = accommodationInfoRepository.findAll();
	    List<AccommodationInfoDto> dtoList = new ArrayList<>();

	    for (AccommodationInfo accom : list) {
	        // 필터링 조건
	        if (address != null && !address.equals("전체") && !address.equals(accom.getAccommodationAddress())) {
	            continue;
	        }

	        if (type != null && !type.equals(accom.getAccommodationType())) {
	            continue;
	        }

	        AccommodationInfoDto dto = new AccommodationInfoDto();
	        dto.setAccommodation_no(accom.getAccommodationNo());
	        dto.setAccommodation_name(accom.getAccommodationName());
	        dto.setAccommodation_type(accom.getAccommodationType());
	        dto.setAccommodation_address(accom.getAccommodationAddress());
	        dto.setAccommodation_phone(accom.getAccommodationPhone());
	        dto.setAccommodation_email(accom.getAccommodationEmail());
	        dto.setAccommodation_site(accom.getAccommodationSite());
	        dto.setAccommodation_location(accom.getAccommodationLocation());
	        dto.setAccommodation_content(accom.getAccommodationContent());
	        dto.setRoom_price(accom.getRoomPrice());
	        dto.setReg_date(accom.getRegDate());
	        dto.setMod_date(accom.getModDate());

	        // 이미지 1장만 추가
	        List<AccommodationAttach> attachList = accommodationAttachRepository
	            .findByAccommodationInfo_AccommodationNo(accom.getAccommodationNo());
	        if (!attachList.isEmpty()) {
	            AccommodationAttach attach = attachList.get(0);
	            AccommodationAttachDto attachDto = new AccommodationAttachDto();
	            attachDto.setAttach_no(attach.getAttachNo());
	            attachDto.setNew_name(attach.getNewName());
	            attachDto.setAttach_path(attach.getAttachPath());
	            attachDto.setAccommodation_no(accom.getAccommodationNo());
	            dto.setAttachList(List.of(attachDto));
	        }

	        dtoList.add(dto);
	    }

	    // 가격 정렬
	    if ("asc".equals(sort)) {
	        dtoList.sort(Comparator.comparing(AccommodationInfoDto::getRoom_price));
	    } else if ("desc".equals(sort)) {
	        dtoList.sort(Comparator.comparing(AccommodationInfoDto::getRoom_price).reversed());
	    }

	    return dtoList;
	}





 


}

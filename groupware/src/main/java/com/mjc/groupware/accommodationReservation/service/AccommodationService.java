package com.mjc.groupware.accommodationReservation.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mjc.groupware.accommodationReservation.dto.AccommodationAttachDto;
import com.mjc.groupware.accommodationReservation.dto.AccommodationInfoDto;
import com.mjc.groupware.accommodationReservation.dto.PageDto;
import com.mjc.groupware.accommodationReservation.dto.SearchDto;
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
	        dto.setAccommodation_type_name(dto.getAccommodationTypeName());
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
	
    // 숙소 상세(관리자)
    public AccommodationInfoDto findById(Long accommodationNo) {
        AccommodationInfo accom = accommodationInfoRepository.findById(accommodationNo)
            .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));
        
        AccommodationInfoDto dto = new AccommodationInfoDto();
        dto.setAccommodation_no(accom.getAccommodationNo());
        dto.setAccommodation_name(accom.getAccommodationName());
        dto.setAccommodation_type(accom.getAccommodationType());
        dto.setAccommodation_type_name(dto.getAccommodationTypeName());
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
    	entity.setAccommodationContent(dto.getAccommodation_content());
    	entity.setRoomPrice(dto.getRoom_price());
    	entity.setModDate(dto.getMod_date());
    	
    	return accommodationInfoRepository.save(entity); // 변경 후 저장
    }

    // 숙소 삭제(관리자)
	public void deleteAccommodation(Long accommodationNo) {
		accommodationInfoRepository.deleteById(accommodationNo);
	}

	// 필터링,페이징처리
	public Page<AccommodationInfoDto> getFilteredList(SearchDto searchDto,PageDto pageDto) {
		Pageable pageable = PageRequest.of(pageDto.getNowPage()-1, pageDto.getNumPerPage());
		
        List<AccommodationInfo> list = accommodationInfoRepository.findAll();
        List<AccommodationInfoDto> dtoList = new ArrayList<>();

        for (AccommodationInfo accom : list) {
            AccommodationInfoDto dto = new AccommodationInfoDto().toDto(accom);

            // 지역필터
            if(searchDto.getAccommodation_address() != null &&
            		!searchDto.getAccommodation_address().equals("전체") &&
            		(dto.getAccommodation_address() == null || !dto.getAccommodation_address().startsWith(searchDto.getAccommodation_address()))) {
            	continue;
            }
            
        	// 유형필터
            if(searchDto.getAccommodation_type() != null &&
            		!searchDto.getAccommodation_type().equals(dto.getAccommodationTypeName())) {
            	continue;
            }
            
            // 출력용 이름 설정
            dto.setAccommodation_type(dto.getAccommodationTypeName());

            // 이미지 출력
            List<AccommodationAttach> attachList = accommodationAttachRepository.findByAccommodationInfo_AccommodationNo(accom.getAccommodationNo());
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

        // 가격필터
        if ("asc".equals(searchDto.getRoom_price_sort())) {
            dtoList.sort(Comparator.comparing(AccommodationInfoDto::getRoom_price));
        } else if ("desc".equals(searchDto.getRoom_price_sort())) {
            dtoList.sort(Comparator.comparing(AccommodationInfoDto::getRoom_price).reversed());
        }
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start+pageable.getPageSize(), dtoList.size());

        if (start >= dtoList.size()) {
            return new PageImpl<>(List.of(), pageable, dtoList.size());
        }
        List<AccommodationInfoDto> pageContent = dtoList.subList(start, end);
        return new PageImpl<>(pageContent,pageable,dtoList.size());
    }



}

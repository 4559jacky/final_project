package com.mjc.groupware.accommodationReservation.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

        List<AccommodationAttach> attachList = accommodationAttachRepository
                .findByAccommodationInfo_AccommodationNo(accommodationNo);
            dto.setAttachList(attachList);
        
        return dto;
    }

	public List<AccommodationAttach> findAttachList(Long accommodationNo) {
		 return accommodationAttachRepository.findByAccommodationInfo_AccommodationNo(accommodationNo);
	}



}

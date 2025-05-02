package com.mjc.groupware.accommodationReservation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mjc.groupware.accommodationReservation.dto.AccommodationInfoDto;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;
import com.mjc.groupware.accommodationReservation.repository.AccommodationInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationService {

	private final AccommodationInfoRepository accommodationInfoRepository;

	// 숙소등록
    public void register(AccommodationInfoDto dto) {
        AccommodationInfo entity = dto.toEntity();
        accommodationInfoRepository.save(entity);
    }
    
    // 목록출력
    public List<AccommodationInfo> getAllAccommodations() {
        //return accommodationInfoRepository.findAll();
    	List<AccommodationInfo> list = accommodationInfoRepository.findAll();
        System.out.println("조회된 숙소 수: " + list.size());
        for (AccommodationInfo info : list) {
            System.out.println(info.getAccommodationName());
        }
        return list;
    }

    //
    
    
    
}

package com.mjc.groupware.accommodationReservation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.accommodationReservation.dto.AccommodationReservationDto;
import com.mjc.groupware.accommodationReservation.entity.AccommodationReservation;
import com.mjc.groupware.accommodationReservation.repository.AccommodationReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationReservationService {

	private final AccommodationReservationRepository reservationRepository;

    // 예약 등록
    public void createReservation(AccommodationReservationDto dto) {
        reservationRepository.save(dto.toEntity());
    }

    // 사용자 예약 내역
    public List<AccommodationReservationDto> getReservationsByMember(Long memberNo) {
        List<AccommodationReservation> list = reservationRepository.findByMember_MemberNo(memberNo);
        List<AccommodationReservationDto> dtoList = new ArrayList<>();

        for (AccommodationReservation reservation : list) {
            AccommodationReservationDto dto = new AccommodationReservationDto();

            dto.setReservation_no(reservation.getReservationNo());
            dto.setNumber_of_people(reservation.getNumberOfPeople());
            dto.setReservation_date(reservation.getReservationDate());
            dto.setCheck_in(reservation.getCheckIn());
            dto.setCheck_out(reservation.getCheckOut());
            dto.setReservation_status(reservation.getReservationStatus());

            if (reservation.getMember() != null) {
                dto.setMember_no(reservation.getMember().getMemberNo());
                dto.setMember_name(reservation.getMember().getMemberName());
            }

            if (reservation.getAccommodationInfo() != null) {
                dto.setAccommodation_no(reservation.getAccommodationInfo().getAccommodationNo());
                dto.setAccommodation_name(reservation.getAccommodationInfo().getAccommodationName());
                dto.setRoom_price(reservation.getAccommodationInfo().getRoomPrice());
                dto.setRoom_count(reservation.getAccommodationInfo().getRoomCount());
            }

            dtoList.add(dto);
        }

        return dtoList;
    }

    // 관리자 전체 예약 조회
	public List<AccommodationReservationDto> getReservationsByAccommodation(Long accommodationNo) {
		List<AccommodationReservation> reservations = reservationRepository.findByAccommodationInfo_AccommodationNo(accommodationNo);
	    List<AccommodationReservationDto> dtoList = new ArrayList<>();

	    for (AccommodationReservation reservation : reservations) {
	    	
	        AccommodationReservationDto dto = new AccommodationReservationDto();

	        dto.setReservation_no(reservation.getReservationNo());
	        dto.setNumber_of_people(reservation.getNumberOfPeople());
	        dto.setReservation_date(reservation.getReservationDate());
	        dto.setCheck_in(reservation.getCheckIn());
	        dto.setCheck_out(reservation.getCheckOut());
	        dto.setReservation_status(reservation.getReservationStatus());

	        if (reservation.getMember() != null) {
	            dto.setMember_no(reservation.getMember().getMemberNo());
	            dto.setMember_name(reservation.getMember().getMemberName());
	        }

	        if (reservation.getAccommodationInfo() != null) {
	            dto.setAccommodation_no(reservation.getAccommodationInfo().getAccommodationNo());
	            dto.setAccommodation_name(reservation.getAccommodationInfo().getAccommodationName());
	            dto.setRoom_price(reservation.getAccommodationInfo().getRoomPrice());
	            dto.setRoom_count(reservation.getAccommodationInfo().getRoomCount());
	        }

	        dtoList.add(dto);
	    }

	    return dtoList;
	}

	// 예약 상태변경
	@Transactional	
	public void updateReservationStatus(Long reservationNo, int status) {
		AccommodationReservation reservation = reservationRepository.findById(reservationNo)
		        .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));
		    String reserveStatus = "";
			if(status == 1) {
				reserveStatus = "대기";
			} else if(status == 2) {
				reserveStatus = "승인";
			} else if(status == 3) {
				reserveStatus = "반려";
			}
		    reservation.setReservationStatus(reserveStatus); // "대기", "승인" 또는 "반려"
		    
		    // 강제 flush
		    reservationRepository.flush();
	}


}

package com.mjc.groupware.accommodationReservation.service;

import java.util.ArrayList;
import java.util.Comparator;
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
            dto.setRoom_count(reservation.getRoomCount());
            dto.setReject_reason(reservation.getRejectReason());


            if (reservation.getMember() != null) {
                dto.setMember_no(reservation.getMember().getMemberNo());
                dto.setMember_name(reservation.getMember().getMemberName());
            }

            if (reservation.getAccommodationInfo() != null) {
                dto.setAccommodation_no(reservation.getAccommodationInfo().getAccommodationNo());
                dto.setAccommodation_name(reservation.getAccommodationInfo().getAccommodationName());
                dto.setRoom_price(reservation.getAccommodationInfo().getRoomPrice());
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
	        dto.setRoom_count(reservation.getRoomCount());
	        dto.setReject_reason(reservation.getRejectReason());

	        if (reservation.getMember() != null) {
	            dto.setMember_no(reservation.getMember().getMemberNo());
	            dto.setMember_name(reservation.getMember().getMemberName());
	        }

	        if (reservation.getAccommodationInfo() != null) {
	            dto.setAccommodation_no(reservation.getAccommodationInfo().getAccommodationNo());
	            dto.setAccommodation_name(reservation.getAccommodationInfo().getAccommodationName());
	            dto.setRoom_price(reservation.getAccommodationInfo().getRoomPrice());
	        }

	        dtoList.add(dto);
	    }

	    return dtoList;
	}
	
	// 예약 상태변경
	@Transactional	

	public void updateReservationStatus(Long reservationNo, int status, String rejectReason) {

		AccommodationReservation reservation = reservationRepository.findById(reservationNo)
		        .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));
		
		String reserveStatus;
	    if (status == 1) {
	        reserveStatus = "대기";
	        reservation.setRejectReason(null);
	    } else if (status == 2) {
	        reserveStatus = "승인";
	        reservation.setRejectReason(null);
	    } else if (status == 3) {
	        reserveStatus = "반려";
	        reservation.setRejectReason(rejectReason); // 반려사유 저장
	    } else {
	        throw new IllegalArgumentException("유효하지 않은 상태 코드입니다.");
	    }

	    reservation.setReservationStatus(reserveStatus); // "대기", "승인" 또는 "반려"
	    reservationRepository.flush(); // 강제 flush
	}

	public AccommodationReservationDto findLatestByAccommodationNo(Long accommodationNo) {
	    return reservationRepository
	            .findTopByAccommodationInfo_AccommodationNoOrderByReservationDateDesc(accommodationNo)
	            .map(reservation -> {
	                AccommodationReservationDto dto = new AccommodationReservationDto();

	                dto.setReservation_no(reservation.getReservationNo());
	                dto.setNumber_of_people(reservation.getNumberOfPeople());
	                dto.setReservation_date(reservation.getReservationDate());
	                dto.setCheck_in(reservation.getCheckIn());
	                dto.setCheck_out(reservation.getCheckOut());
	                dto.setReservation_status(reservation.getReservationStatus());
	                dto.setRoom_count(reservation.getRoomCount());
	                dto.setReject_reason(reservation.getRejectReason());

	                if (reservation.getMember() != null) {
	                    dto.setMember_no(reservation.getMember().getMemberNo());
	                    dto.setMember_name(reservation.getMember().getMemberName());
	                }

	                if (reservation.getAccommodationInfo() != null) {
	                    dto.setAccommodation_no(reservation.getAccommodationInfo().getAccommodationNo());
	                    dto.setAccommodation_name(reservation.getAccommodationInfo().getAccommodationName());
	                    dto.setRoom_price(reservation.getAccommodationInfo().getRoomPrice());
	                }

	                return dto;
	            })
	            .orElse(null);
	}

	// 정렬된 사용자 예약 내역 조회
	public List<AccommodationReservationDto> getReservationsByMemberSorted(Long memberNo, String regDateSort) {
	    List<AccommodationReservation> list = reservationRepository.findByMember_MemberNo(memberNo);
	    List<AccommodationReservationDto> dtoList = new ArrayList<>();

	    for (AccommodationReservation reservation : list) {
	        AccommodationReservationDto dto = new AccommodationReservationDto().toDto(reservation);
	        dtoList.add(dto);
	    }

	    // 정렬
	    if ("asc".equals(regDateSort)) {
	        dtoList.sort(Comparator.comparing(AccommodationReservationDto::getReservation_date));
	    } else if ("desc".equals(regDateSort)) {
	        dtoList.sort(Comparator.comparing(AccommodationReservationDto::getReservation_date).reversed());
	    }

	    return dtoList;
	}



}

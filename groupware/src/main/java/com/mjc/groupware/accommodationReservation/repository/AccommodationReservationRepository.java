package com.mjc.groupware.accommodationReservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.accommodationReservation.entity.AccommodationReservation;

public interface AccommodationReservationRepository extends JpaRepository<AccommodationReservation, Long> {
    
	// 사용자
	List<AccommodationReservation> findByMember_MemberNo(Long memberNo);

	// 관리자 
	List<AccommodationReservation> findByAccommodationInfo_AccommodationNo(Long accommodationNo);

}

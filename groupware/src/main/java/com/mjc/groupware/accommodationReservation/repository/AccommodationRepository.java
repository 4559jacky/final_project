package com.mjc.groupware.accommodationReservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.accommodationReservation.entity.AccommodationReservation;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;

public interface AccommodationRepository extends JpaRepository<AccommodationReservation,Long>{

	//숙소등록
	AccommodationInfo save(AccommodationInfo entity);

}

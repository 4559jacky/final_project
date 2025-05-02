package com.mjc.groupware.accommodationReservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.accommodationReservation.entity.Accommodation;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;

public interface AccommodationRepository extends JpaRepository<Accommodation,Long>{

	//숙소등록
	AccommodationInfo save(AccommodationInfo entity);

}

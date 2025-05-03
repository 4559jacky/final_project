package com.mjc.groupware.accommodationReservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.accommodationReservation.entity.AccommodationAttach;

public interface AccommodationAttachRepository extends JpaRepository<AccommodationAttach, Long> {

//	List<AccommodationAttach> findByAccommodationNo(AccommodationAttach accommodationAttach);
	List<AccommodationAttach> findByAccommodationInfo_AccommodationNo(Long accommodationNo);

}


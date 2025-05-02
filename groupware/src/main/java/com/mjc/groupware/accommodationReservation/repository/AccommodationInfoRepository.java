package com.mjc.groupware.accommodationReservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;

@Repository
public interface AccommodationInfoRepository extends JpaRepository<AccommodationInfo, Long> {

}


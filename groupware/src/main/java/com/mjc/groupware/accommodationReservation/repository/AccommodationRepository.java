package com.mjc.groupware.accommodationReservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.accommodationReservation.entity.Accommodation;

public interface AccommodationRepository extends JpaRepository<Accommodation,Long>{

}

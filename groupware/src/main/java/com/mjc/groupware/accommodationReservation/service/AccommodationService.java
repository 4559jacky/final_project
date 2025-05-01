package com.mjc.groupware.accommodationReservation.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.accommodationReservation.repository.AccommodationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationService {

	private final AccommodationRepository repository;
	
	public void saveAccommodation(String name, String type, String address, String phone, String email, String site,
			String price, String content, MultipartFile imageFile) {
		
	}

}

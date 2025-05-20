package com.mjc.groupware.address.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mjc.groupware.address.entity.Address;
import com.mjc.groupware.address.repository.AddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {
	
	private final AddressRepository repository;
	
	public List<Address> selectAddressAll() {
		List<Address> resultList = repository.findAll();
		
		return resultList;
	}
	
	public List<String> selectAddr1Distinct() {
		List<String> resultList = repository.findDistinctAddr1();
		
		return resultList;
	}
	
	public List<String> selectAddr2ByAddr1(String addr1) {
		if (addr1 == null || addr1.isEmpty()) {
            return List.of();
        }
		
		List<String> resultList = repository.findAddr2ByAddr1(addr1);
		
		return Optional.ofNullable(resultList).orElse(List.of());
	}
	
}

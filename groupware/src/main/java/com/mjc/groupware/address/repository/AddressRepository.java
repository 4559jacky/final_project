package com.mjc.groupware.address.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mjc.groupware.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
	
	 @Query("SELECT DISTINCT a.addr1 FROM Address a ORDER BY a.addr1")
	 List<String> findDistinctAddr1();

}

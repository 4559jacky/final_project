package com.mjc.groupware.address.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
	
	@Query("SELECT DISTINCT a.addr1 FROM Address a ORDER BY a.addr1")
	List<String> findDistinctAddr1();
	
	@Query("SELECT a.addr2 FROM Address a WHERE a.addr1 = :addr1 ORDER BY a.addr2")
	List<String> findAddr2ByAddr1(@Param("addr1") String addr1);
	
}

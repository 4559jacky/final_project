package com.mjc.groupware.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.company.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long>{
	
	Company findTop1ByOrderByRegDateDesc();
	
}

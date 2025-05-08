package com.mjc.groupware.shared.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.mjc.groupware.shared.repository.FileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SharedChartService {
	 private final FileRepository fileRepository;
	 private static final long TOTAL_CAPACITY_BYTES = 1048576L; // 1MB
	 
	    public Map<String, Long> getUsageStats() {
	    	long active = fileRepository.sumFileSizeByStatus("N"); // 문서함
	        long trash = fileRepository.sumFileSizeByStatus("Y");  // 휴지통
	        long remain = Math.max(0, TOTAL_CAPACITY_BYTES - active - trash); // 남은 용량

	        return Map.of(
	            "active", active,
	            "trash", trash,
	            "remain", remain
	        );
	    }
}

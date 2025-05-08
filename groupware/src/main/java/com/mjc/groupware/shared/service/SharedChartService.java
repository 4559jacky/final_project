package com.mjc.groupware.shared.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.shared.repository.FileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SharedChartService {
	 private final FileRepository fileRepository;
	 private static final long TOTAL_CAPACITY_BYTES = 1048576L; // 1MB
	 
	 public Map<String, Long> getUsageStats(String type, Member member) {
	        long active, trash;
	        if (type.equals("personal")) {
	            active = fileRepository.sumFileSizeByStatusAndMember("N", member.getMemberNo());
	            trash = fileRepository.sumFileSizeByStatusAndMember("Y", member.getMemberNo());
	        } else if (type.equals("department")) {
	            Long deptNo = member.getDept().getDeptNo();
	            active = fileRepository.sumFileSizeByStatusAndDept("N", deptNo);
	            trash = fileRepository.sumFileSizeByStatusAndDept("Y", deptNo);
	        } else {
	            active = fileRepository.sumFileSizeByStatusAndType("N", 3);
	            trash = fileRepository.sumFileSizeByStatusAndType("Y", 3);
	        }
	        long remain = Math.max(0, TOTAL_CAPACITY_BYTES - active - trash);
	        return Map.of("active", active, "trash", trash, "remain", remain);
	    }
}

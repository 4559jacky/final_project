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
	 private static final long TOTAL_CAPACITY_BYTES = 500L * 1024 * 1024; // 500MB
	 
	 public Map<String, Long> getUsageStats(String type, Member member) {
		    long active = 0;
		    long trash = 0;

		    switch (type) {
		    case "personal":
		        active = fileRepository.sumSizeByOwnerAndStatus(member.getMemberNo(), "N");
		        trash = fileRepository.sumSizeByOwnerAndStatus(member.getMemberNo(), "Y");
		        break;
		    case "department":
		        active = fileRepository.sumSizeByDeptAndStatus(member.getDept().getDeptNo(), "N");
		        trash = fileRepository.sumSizeByDeptAndStatus(member.getDept().getDeptNo(), "Y");
		        break;
		    case "public":
		        active = fileRepository.sumSizeByTypeAndStatus("N");
		        trash = fileRepository.sumSizeByTypeAndStatus("Y");
		        break;
		}
		    long remain = Math.max(0, TOTAL_CAPACITY_BYTES - active - trash);
		    return Map.of("active", active, "trash", trash, "remain", remain);
		}
}

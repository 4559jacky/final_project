package com.mjc.groupware.member.service;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.member.dto.MemberAttachDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.entity.MemberAttach;
import com.mjc.groupware.member.repository.MemberAttachRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberAttachService {
	
	@Value("${ffupload.location}")
	private String fileDir;
	
	private Logger logger = LoggerFactory.getLogger(MemberAttachService.class);
	
	private final MemberAttachRepository repository;
	
	public MemberAttachDto uploadFile(MultipartFile file) {
		MemberAttachDto dto = new MemberAttachDto();
		
		try {
			if(file == null || file.isEmpty()) {
				throw new Exception("존재하지 않는 파일입니다.");
			}
			
			long fileSize = file.getSize();
			if(fileSize <= 0 || 2 * 1024 * 1024 < fileSize) {
				throw new Exception("파일 크기 : " + fileSize +"(파일 사이즈가 2MB를 넘습니다.");
			}
			
			String oriName = file.getOriginalFilename();
			dto.setOri_name(oriName);
			
			String fileExt = oriName.substring(oriName.lastIndexOf("."), oriName.length());
			
			String newName = UUID.randomUUID().toString().replaceAll("-", "") + fileExt;
			dto.setNew_name(newName);
			
			String downDir = fileDir+newName;
			dto.setAttach_path(downDir);
			
			File saveFile = new File(downDir);
			
			if(!saveFile.exists()) {
				saveFile.mkdirs();
			}
			
			file.transferTo(saveFile);
			
		} catch(Exception e) {
			dto = null;
			logger.error("프로필 사진 수정 중 오류 발생", e);
		}
		
		return dto;
	}
	
	public void updateMyProfile(MemberAttachDto dto) {
		try {
			MemberAttach param = MemberAttach.builder()
					.attachNo(dto.getAttach_no())
					.oriName(dto.getOri_name())
					.newName(dto.getNew_name())
					.attachPath(dto.getAttach_path())
					.member(Member.builder().memberNo(dto.getMember_no()).build())
					.build();
			
			repository.save(param);
			
		} catch(Exception e) {
			logger.error("프로필 사진 수정 중 오류 발생", e);
		}
	}
	
	public MemberAttachDto selectLatestMyProfile(Member member) {
		// 가장 마지막에 등록된 사원 프로필 이미지를 뽑아내는 로직
		MemberAttach latest = repository.findTop1ByMemberOrderByRegDateDesc(member);
		
		if(latest == null) {
			return null;
		}
		
		return MemberAttachDto.builder()
				.attach_no(latest.getAttachNo())
				.ori_name(latest.getOriName())
				.new_name(latest.getNewName())
				.attach_path(latest.getAttachPath())
				.reg_date(latest.getRegDate())
				.mod_date(latest.getModDate())
				.build();
	}
	
}

package com.mjc.groupware.shared.service;



import org.springframework.stereotype.Service;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.shared.dto.SharedFolderDto;
import com.mjc.groupware.shared.entity.SharedFolder;
import com.mjc.groupware.shared.repository.FolderRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SharedFolderService {
	
	private final FolderRepository folderRepository;
	private final MemberRepository memberRepository;
	
	// 폴더 생성.
	public void createFolder(SharedFolderDto dto) {
		 SharedFolder folder = dto.toEntity(); // member + parent까지 다 포함된 entity
		

		 folder.setFolderStatus("N");
		 
		 Member member = memberRepository.findById(dto.getMember_no())
				 .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
		 
		 folder.setMember(member);
		 
		 if(member.getDept() != null) {
			 folder.setDept(member.getDept());
		 }
		 if(member.getPos() != null) {
			 folder.setPos(member.getPos());
		 }

		 folderRepository.save(folder);
	}
}



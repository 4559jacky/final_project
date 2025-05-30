package com.mjc.groupware.approval.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.approval.entity.ApprovalAttach;
import com.mjc.groupware.approval.repository.ApprovalAttachRepository;
import com.mjc.groupware.notice.entity.Attach;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalAttachService {
	
	private final ApprovalAttachRepository approvalAttachRepository;
	
	@Value("${ffupload.location}")
	private String filePath;
	
	
	public void saveFile(MultipartFile file, Approval approval) throws IOException {
        if (!file.isEmpty()) {

        	String approvalFilePath = filePath + "/approval";
            String ori = file.getOriginalFilename();
            String newName = UUID.randomUUID().toString() + "_" + ori;
            String path = approvalFilePath + newName;

            File uploadDir = new File(approvalFilePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            file.transferTo(new File(path));

            ApprovalAttach attach = ApprovalAttach.builder()
                    .oriName(ori)
                    .newName(newName)
                    .attachPath(path)
                    .approval(approval)
                    .regDate(LocalDateTime.now())
                    .build();

            approvalAttachRepository.save(attach);
        }
    }
	
	public void deleteAttachById(Long id) {
        approvalAttachRepository.findById(id).ifPresent(attach -> {
            File file = new File(attach.getAttachPath());
            if (file.exists()) {
                file.delete();
            }
            approvalAttachRepository.deleteById(id);
        });
    }
	
	public List<ApprovalAttach> findByApproval(Approval approval) {
		List<ApprovalAttach> attachList = approvalAttachRepository.findByApproval(approval);
		for(ApprovalAttach a : attachList) {
			System.out.println("원래 파일 이름 : "+a.getOriName());
		}
		return attachList;
	}
	
}

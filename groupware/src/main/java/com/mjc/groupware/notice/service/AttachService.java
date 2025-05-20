package com.mjc.groupware.notice.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.notice.entity.Attach;
import com.mjc.groupware.notice.entity.Notice;
import com.mjc.groupware.notice.repository.AttachRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachService {

	@Value("${ffupload.location}")
	private String filePath;
	
    private final AttachRepository attachRepository;

    public void saveFile(MultipartFile file, Notice notice) throws IOException {
        if (!file.isEmpty()) {
            String ori = file.getOriginalFilename();
            String newName = UUID.randomUUID().toString() + "_" + ori;
            String path = filePath + newName;

            File uploadDir = new File(filePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            file.transferTo(new File(path));

            Attach attach = Attach.builder()
                    .oriName(ori)
                    .newName(newName)
                    .attachPath(path)
                    .notice(notice)
                    .regDate(LocalDateTime.now())
                    .build();

            attachRepository.save(attach);
        }
    }
    
    public void deleteAttachById(Long id) {
        attachRepository.findById(id).ifPresent(attach -> {
            File file = new File(attach.getAttachPath());
            if (file.exists()) {
                file.delete();
            }
            attachRepository.deleteById(id);
        });
    }
}
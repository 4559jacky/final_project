package com.mjc.groupware.shared.service;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.shared.dto.SharedFileDto;
import com.mjc.groupware.shared.entity.SharedFile;
import com.mjc.groupware.shared.repository.FileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${ffupload.location}") // 📁 실제 파일이 저장될 루트 경로 (yml에서 설정)
    private String fileDir;

    private final FileRepository fileRepository; //  DB 저장용 JPA 리포지토리

    public SharedFileDto uploadFile(MultipartFile file, Member member) {
    	SharedFileDto dto = new SharedFileDto(); //  반환할 DTO 객체 준비

        try {
            // 1. 파일 유효성 검사
            if (file == null || file.isEmpty()) {
                throw new Exception("존재하지 않는 파일입니다.");
            }

            // 2. 파일 용량 체크 (1MB 제한)
            long fileSize = file.getSize();
            if (fileSize > 1048576) {
                throw new Exception("허용 용량을 초과한 파일입니다.");
            }

            // 3. 원본 파일명, 확장자 추출
            String oriName = file.getOriginalFilename();
            String fileExt = oriName.substring(oriName.lastIndexOf("."));

            // 4. 새로운 고유 파일명 생성 (UUID 사용)
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String newName = uuid + fileExt;

            // 5. 저장 경로 설정
            String saveDir = fileDir + "shared/";
            String fullPath = saveDir + newName;

            // 6. 저장 경로 없으면 생성
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 7. 실제 파일 저장
            File saveFile = new File(fullPath);
            file.transferTo(saveFile);

            // 8. Entity 생성 및 DB 저장
            SharedFile entity = SharedFile.builder()
                    .fileName(oriName)
                    .fileNewName(newName)
                    .filePath(fullPath)
                    .member(member)
                    .build();

            SharedFile saved = fileRepository.save(entity); // ✅ DB 저장

            // 9. DTO로 반환
            dto = SharedFileDto.builder()
                    .file_no(saved.getFileNo())
                    .file_name(saved.getFileName())
                    .file_new_name(saved.getFileNewName())
                    .file_path(saved.getFilePath())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            dto = null; // ❗ 실패 시 null 반환
        }

        return dto;
    }
}


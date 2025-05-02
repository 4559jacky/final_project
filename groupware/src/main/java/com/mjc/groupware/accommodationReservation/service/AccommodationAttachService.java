package com.mjc.groupware.accommodationReservation.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.accommodationReservation.entity.AccommodationAttach;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;
import com.mjc.groupware.accommodationReservation.repository.AccommodationAttachRepository;
import com.mjc.groupware.accommodationReservation.repository.AccommodationInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationAttachService {

//	private final AccommodationAttachRepository accommodationAttachRepository;
//	private final AccommodationInfoRepository accommodationInfoRepository;
//	
//	public void saveFile(MultipartFile file, Long accommodationNo) throws IOException {
//        if (!file.isEmpty()) {
//            String ori = file.getOriginalFilename();
//            String newName = UUID.randomUUID().toString() + "_" + ori;
//            String path = "C:/upload/accommodation/" + newName;
//
//            File uploadDir = new File("C:/upload/accommodation/");
//            if (!uploadDir.exists()) {
//                uploadDir.mkdirs();
//            }
//
//            file.transferTo(new File(path));
//
//            // accommodationNo로 AccommodationInfo 객체 조회
//            AccommodationInfo accommodationInfo = accommodationInfoRepository.findById(accommodationNo)
//                    .orElseThrow(() -> new IllegalArgumentException("해당 숙소가 존재하지 않습니다."));
//            
//            AccommodationAttach attach = AccommodationAttach.builder()
//                    .oriName(ori)
//                    .newName(newName)
//                    .attachPath(path)
//                    .accommodationNo(accommodationInfo)
//                    .regDate(LocalDateTime.now())
//                    .build();
//
//            accommodationAttachRepository.save(attach);
//        }
//    }
//    
//    public void deleteAttachById(Long id) {
//    	accommodationAttachRepository.findById(id).ifPresent(attach -> {
//            File file = new File(attach.getAttachPath());
//            if (file.exists()) {
//                file.delete();
//            }
//            accommodationAttachRepository.deleteById(id);
//        });
//    }
	
}

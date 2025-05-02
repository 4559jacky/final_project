package com.mjc.groupware.approval.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.approval.entity.ApprovalAttach;
import com.mjc.groupware.approval.repository.ApprovalAttachRepository;
import com.mjc.groupware.approval.service.ApprovalAttachService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ApprovalAttachController {
	private final ApprovalAttachRepository approvalAttachRepository;
	private final ApprovalAttachService approvalAttachService;
	
	@GetMapping("/approval/attach/download")
	public ResponseEntity<Resource> downloadFile(@RequestParam("fileNo") Long fileNo) throws IOException {
		ApprovalAttach attach = approvalAttachRepository.findById(fileNo)
						.orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다."));
		Path path = Paths.get(attach.getAttachPath());
		if(!Files.exists(path)) {
			throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + attach.getOriName());
		}
		Resource resource = new InputStreamResource(Files.newInputStream(path));
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(attach.getOriName(), "UTF-8") + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
	}
	
	@DeleteMapping("/approval/attach/delete/{id}")
    @ResponseBody
    public Map<String, String> deleteAttach(@PathVariable("id") Long id) {
        Map<String, String> result = new HashMap<>();
        result.put("res_code", "500");
        result.put("res_msg", "삭제 실패");

        try {
        	approvalAttachService.deleteAttachById(id);
            result.put("res_code", "200");
            result.put("res_msg", "삭제 성공");
        } catch (Exception e) {
            result.put("res_msg", "오류: " + e.getMessage());
        }

        return result;
    }
}

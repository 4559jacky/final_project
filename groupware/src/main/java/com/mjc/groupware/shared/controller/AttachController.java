package com.mjc.groupware.shared.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mjc.groupware.shared.entity.Attach;
import com.mjc.groupware.shared.service.AttachService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AttachController {
	
	private final AttachService service;
	
	@GetMapping("/download/{id}")
	public ResponseEntity<Object> fileDownload(@PathVariable("id") Long id){
		try {
			Attach fileData = service.selectAttachOne(id);
			if(fileData == null) {
				return ResponseEntity.notFound().build();
			}
			Path filePath = Paths.get(fileData.getAttachPath());
			Resource resource = new InputStreamResource(Files.newInputStream(filePath));
			String oriFileName = fileData.getOriName();
			String encodedName = URLEncoder.encode(oriFileName,StandardCharsets.UTF_8);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+encodedName);
			
			return new ResponseEntity<Object>(resource,headers,HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
}

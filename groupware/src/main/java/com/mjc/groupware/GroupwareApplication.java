package com.mjc.groupware;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class GroupwareApplication implements WebMvcConfigurer {
	
	@Value("${ffupload.location}")
	private String fileDir;
	
	public static void main(String[] args) {
		SpringApplication.run(GroupwareApplication.class, args);
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!fileDir.endsWith(File.separator)) {
	        fileDir += File.separator;
	    }
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations("file:" + fileDir);
	}

}

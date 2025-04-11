package com.mjc.groupware.dept.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeptTreeDto {
	
    private String id;
    private String parent;
    private String text;
    private String type;
    private Map<String, String> a_attr;
    
}
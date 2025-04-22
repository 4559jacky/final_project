package com.mjc.groupware.approval.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mjc.groupware.approval.mybatis.vo.ApprovalVo;

@Mapper
public interface ApprovalMapper {
	// 메소드명과 mapper.xml 파일의 id 맞춰주기
	List<ApprovalVo> selectApprovalAllByMemberNo(Map<String,Object> paramMap);
}

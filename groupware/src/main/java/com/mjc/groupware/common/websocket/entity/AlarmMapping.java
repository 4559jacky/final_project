package com.mjc.groupware.common.websocket.entity;

import com.mjc.groupware.member.entity.Member;

import groovy.transform.ToString;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="alarm_mapping")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmMapping {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="alarm_mapping_no")
	private Long alarmMappingNo;
	
	@ManyToOne
	@JoinColumn(name="alarm_no")
	private Alarm alarm;
	
	@ManyToOne
	@JoinColumn(name="member_no")
	private Member member;
	
	@Column(name="read_yn")
	private String readYn;
	
//	@Column(name="delete_yn")
//	private String deleteYn;
	
	public void updateReadYn(String status) {
		this.readYn = status;
	}
	
}

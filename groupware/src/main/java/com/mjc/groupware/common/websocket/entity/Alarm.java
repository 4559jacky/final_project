package com.mjc.groupware.common.websocket.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.notice.entity.Notice;

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
@Table(name="alarm")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Alarm {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="alarm_no")
	private Long alarmNo;
	
	@Column(name="alarm_title")
	private String alarmTitle;
	
	@Column(name="alarm_message")
	private String alarmMessage;
	
	@CreationTimestamp
	@Column(name="reg_date")
	private LocalDateTime regDate;
	
	@ManyToOne
	@JoinColumn(name="approval_no")
	private Approval approval;
	
	@ManyToOne
	@JoinColumn(name="notice_no")
	private Notice notice;
	
	@ManyToOne
	@JoinColumn(name="board_no")
	private Board board;
}

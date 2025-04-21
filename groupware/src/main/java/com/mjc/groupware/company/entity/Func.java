package com.mjc.groupware.company.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name="func")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Func {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="func_no")
	private Long funcNo;
	
	@Column(name="func_name")
	private String funcName;
	
	@Column(name="func_url")
	private String funcUrl;
	
	@Column(name="func_status")
	private int funcStatus;
	
	@ManyToOne
	@JoinColumn(name="parent_func_no")
	private Func parentFunc;
	
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
	private LocalDateTime regDate;
	
	@UpdateTimestamp
	@Column(insertable=false,name="mod_date")
	private LocalDateTime modDate;
	
	public void changeFuncStatus(int funcStatus) {
		this.funcStatus = funcStatus;
	}
	
}

package com.mjc.groupware.plan.entity;

import groovy.transform.builder.Builder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="plan")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Plan {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="plan_no")
	public Long planNo;
	
	@Column(name="plan_name")
	public String planName;
	
	public enum Status {
		
	}
	
}

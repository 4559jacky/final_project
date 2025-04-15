package com.mjc.groupware.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="address")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Address {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="address_no")
	public Long addressNo;
	
	@Column(name="addr_1")
	public String addr1;
	
	@Column(name="addr_2")
	public String addr2;
	
}

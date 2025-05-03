package com.mjc.groupware.accommodationReservation.entity;

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
@Table(name="accommodation_attach")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AccommodationAttach {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="attach_no")
	private Long attachNo;
	
	@Column(name="ori_name")
	private String oriName;
	
	@Column(name="new_name")
	private String newName;
	
	@Column(name="attach_path")
	private String attachPath;
	
//	@CreationTimestamp
//	@Column(updatable=false,name="reg_date")
//	private LocalDateTime regDate;
//	
//	@UpdateTimestamp
//	@Column(insertable=false,name="mod_date")
//	private LocalDateTime modDate;
	
	@ManyToOne
	@JoinColumn(name="accommodation_no")
	private AccommodationInfo accommodationInfo;
	
	
}

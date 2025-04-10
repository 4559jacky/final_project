package com.mjc.groupware.shared.entity;

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
import lombok.ToString;

@Entity
@Table(name="shared_attach")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Attach {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long attachNo;
	
	@Column(name="ori_name")
	private String oriName;
	
	@Column(name="new_name")
	private String newName;
	
	@Column(name="attach_path")
	private String attachPath;
	
	@ManyToOne
	@JoinColumn(name="shared_no")
	private Shared shared;
}

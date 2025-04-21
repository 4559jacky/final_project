package com.mjc.groupware.company.entity;

import com.mjc.groupware.member.entity.Role;

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
@Table(name="func_mapping")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FuncMapping {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "func_mapping_no")
    private Long funcMappingNo;
	
	@ManyToOne
	@JoinColumn(name="func_no")
	private Func func;
	
	@ManyToOne
	@JoinColumn(name="role_no")
	private Role role;
	
}

package com.mjc.groupware.member.entity;

import java.util.List;

import com.mjc.groupware.company.entity.FuncMapping;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="role")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Role {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="role_no")
	private Long roleNo;
	
	@Column(name="role_name")
	private String roleName;
	
	@Column(name="role_nickname")
	private String roleNickname;
	
	@OneToMany(mappedBy = "role")
	private List<Member> members;
	
	@OneToMany(mappedBy = "role")
	private List<FuncMapping> funcMappings;
	
}

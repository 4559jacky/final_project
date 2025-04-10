package com.mjc.groupware.shared.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.member.entity.Member;

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
@Table(name="shared_board")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Shared {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="shared_no")
	private Long sharedNo;
	
	@Column(name="shared_title")
	private String sharedTitle;
	
	@Column(name="shared_content")
	private String sharedContent;
	
	@Column(name="views")
	private int views;
	
	@ManyToOne
	@JoinColumn(name="member_no", nullable = false)
	private Member member;
	
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
	private LocalDateTime regDate;
	
	@UpdateTimestamp
	@Column(insertable=false,name="mod_date")
	private LocalDateTime modDate;

	public void update(String title, String content, LocalDateTime modDate) {
		// TODO Auto-generated method stub
		this.sharedTitle = title;
		this.sharedContent = content;
		this.modDate = modDate;
	}
}

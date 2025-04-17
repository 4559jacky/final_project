package com.mjc.groupware.shared.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
import lombok.Setter;

@Entity
@Table(name = "shared_with")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedWith {
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  @Column(name = "with_no")
	  private Long withNo;

	  @ManyToOne
	  @JoinColumn(name = "file_no")
	  private SharedFile file;

	  @ManyToOne
	  @JoinColumn(name = "with_member_no")
	  private Member withMember;

	  @Column(name = "with_permission")
	  private int withPermission;

	  @Column(name = "with_type")
	  private int withType;

	  @CreationTimestamp
	  @Column(name = "reg_date", updatable = false)
	  private LocalDateTime regDate;
}

package com.mjc.groupware.pos.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.member.entity.Member;

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
@Table(name="pos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Pos {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pos_no")
	public Long posNo;
	
	@Column(name="pos_name")
	public String posName;
	
	@Column(name="pos_order")
	public Long posOrder;
	
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
	private LocalDateTime regDate;
	
	@UpdateTimestamp
	@Column(insertable=false,name="mod_date")
	private LocalDateTime modDate;
	
	@OneToMany(mappedBy="pos")
	private List<Member> members;
	
	public void changeOrder(Long posOrder) {
	    this.posOrder = posOrder;
	}
	
	public void changeName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("직급명은 비워둘 수 없습니다.");
        }

        this.posName = newName.trim();
    }
	
}

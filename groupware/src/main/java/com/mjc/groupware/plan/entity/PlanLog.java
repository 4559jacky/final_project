package com.mjc.groupware.plan.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name="plan_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanLog {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "log_no")
	private Long logNo;
	
	// planNo2는 Plan 엔티티의 외래키로 변경
    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY로 설정하여 실제 조회가 필요할 때 로드
    @JoinColumn(name = "plan_no2", referencedColumnName = "plan_no") // 외래키 설정
    private Plan plan; // Plan 객체로 변경

    // memberNo2는 Member 엔티티의 외래키로 변경
    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY로 설정하여 실제 조회가 필요할 때 로드
    @JoinColumn(name = "member_no2", referencedColumnName = "member_no") // 외래키 설정
    private Member member; // Member 객체로 변경

    @Column(name = "action_type")
    private String actionType;

    @UpdateTimestamp
    @Column(insertable = false, name = "action_date")
    private LocalDateTime actionDate;
	
}

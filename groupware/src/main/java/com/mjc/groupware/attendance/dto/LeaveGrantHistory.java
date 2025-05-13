package com.mjc.groupware.attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leave_grant_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LeaveGrantHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Member와 연관관계
    @JoinColumn(name = "member_no")
    private Member member;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;

    public static LeaveGrantHistory create(Member member, LocalDate start, LocalDate end) {
        return LeaveGrantHistory.builder()
                .member(member)
                .startDate(start)
                .endDate(end)
                .grantedAt(LocalDateTime.now())
                .build();
    }
}

package com.mjc.groupware.attendance.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.mjc.groupware.attendance.entity.Attendance;
import com.mjc.groupware.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberAttendanceDto {
	private Member member;
    private Attendance attendance;
    
 // ✨ 뷰에서 사용할 안전한 getter들 추가
    public LocalDate getAttendDate() {
        return attendance != null ? attendance.getAttendDate() : null;
    }

    public LocalTime getStartTime() {
        return attendance != null ? attendance.getCheckIn() : null;
    }

    public LocalTime getEndTime() {
        return attendance != null ? attendance.getCheckOut() : null;
    }

    public String getLateYn() {
        return attendance != null ? attendance.getLateYn() : null;
    }

    public String getEarlyLeaveYn() {
        return attendance != null ? attendance.getEarlyLeaveYn() : null;
    }

    public String getMemberName() {
        return member != null ? member.getMemberName() : "-";
    }

    public Long getMemberNo() {
        return member != null ? member.getMemberNo() : null;
    }
}

package com.mjc.groupware.meetingRoomReservation.dto;

import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservation;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservationMapping;
import com.mjc.groupware.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MeetingRoomReservationMappingDto {
	
	private Long mapping_no;
	private Long reservation_no;
	private Long member_no;
	
	public MeetingRoomReservationMapping toEntity() {
		return MeetingRoomReservationMapping.builder()
										.mappingNo(mapping_no)
										.reservationNo(MeetingRoomReservation.builder().reservationNo(reservation_no).build())
										.memberNo(Member.builder().memberNo(member_no).build())
										.build();
	}
	
}

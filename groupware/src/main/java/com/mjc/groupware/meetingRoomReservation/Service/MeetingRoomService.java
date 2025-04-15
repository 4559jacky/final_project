package com.mjc.groupware.meetingRoomReservation.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.meetingRoomReservation.dto.MeetingRoomDto;
import com.mjc.groupware.meetingRoomReservation.dto.MeetingRoomReservationDto;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoom;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservation;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservationMapping;
import com.mjc.groupware.meetingRoomReservation.repository.MeetingRoomRepository;
import com.mjc.groupware.meetingRoomReservation.repository.MeetingRoomReservationMappingRepository;
import com.mjc.groupware.meetingRoomReservation.repository.MeetingRoomReservationRepository;
import com.mjc.groupware.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {

	private final MeetingRoomRepository repository;
	private final MeetingRoomReservationRepository reservationRepositoty;
	private final MeetingRoomReservationMappingRepository mappingRepository;
	
	
	public List<MeetingRoomReservationDto> selectMeetingRoomReservationAll() {
	    // 모든 예약을 조회
	    List<MeetingRoomReservation> reservations = reservationRepositoty.findAll();

	    // 결과를 저장할 DTO 리스트
	    List<MeetingRoomReservationDto> dtoList = new ArrayList<>();

	    // 예약들을 그룹화하기 위한 Map 선언
	    Map<String, List<MeetingRoomReservation>> groupedMap = new LinkedHashMap<>();

	    // 예약들을 그룹화
	    for (MeetingRoomReservation res : reservations) {
	        String key = res.getMeetingRoomNo().getMeetingRoomNo() + "_" +
	                     res.getMeetingDate() + "_" +
	                     res.getMeetingTitle();

	        groupedMap.computeIfAbsent(key, k -> new ArrayList<>()).add(res);
	    }

	    // 그룹화된 예약들을 DTO로 변환
	    for (List<MeetingRoomReservation> group : groupedMap.values()) {
	        MeetingRoomReservation first = group.get(0);
	        String roomName = first.getMeetingRoomNo().getMeetingRoomName(); // ← 회의실 이름 가져오기
	        
	        Long meetingRoomNo = first.getMeetingRoomNo().getMeetingRoomNo();
	        String title = first.getMeetingTitle();
	        LocalDate date = first.getMeetingDate();

	        List<LocalTime> startTimes = new ArrayList<>();
	        List<Long> memberNos = new ArrayList<>();

	        // 각 예약에서 startTime과 memberNo를 처리
	        for (MeetingRoomReservation r : group) {
	            startTimes.add(r.getMeetingStartTime());

	            // 각 예약에 관련된 Mapping 정보 처리
	            // MeetingRoomReservationMapping은 별도로 조회해서 처리
	            List<MeetingRoomReservationMapping> mappings = mappingRepository
	                    .findByReservationNo(r); // 별도로 Repository에서 조회

	            for (MeetingRoomReservationMapping rm : mappings) {
	                Long memberNo = rm.getMappingNo();
	                if (!memberNos.contains(memberNo)) {
	                    memberNos.add(memberNo);
	                }
	            }
	        }

	        // DTO 생성
	        MeetingRoomReservationDto dto = MeetingRoomReservationDto.builder()
	                .meeting_room_no(meetingRoomNo)
	                .meeting_title(title)
	                .meeting_date(date)
	                .meeting_start_time(startTimes)
	                .member_no(memberNos)
	                .meeting_room_name(roomName)
	                .build();

	        dtoList.add(dto);
	    }

	    return dtoList;
	}


	
	// 사용자 - 회의실 조회, 시간 조회
	public List<MeetingRoom> selectMeetingRoomAll(){
		return repository.findAll();
	}
	

	
	// 사용자 - 회의실 예약 
	@Transactional(rollbackFor = Exception.class)
	public int createMeetingRoomReservation(MeetingRoomReservationDto dto) {
		int result = 0;
		try {
			
			for (LocalTime time : dto.getMeeting_start_time()) {
				// 회의실 예약 저장
				MeetingRoomReservation reservation = MeetingRoomReservation.builder()
						.meetingRoomNo(MeetingRoom.builder().meetingRoomNo(dto.getMeeting_room_no()).build())
						.meetingTitle(dto.getMeeting_title())
						.meetingDate(dto.getMeeting_date())
						.meetingStartTime(time)
						.build();
				
				MeetingRoomReservation saved = reservationRepositoty.save(reservation);
				
				//  예약 번호 받아오기
				Long reservationNo = saved.getReservationNo();
				
				//  참석자 매핑 저장
				for (Long memberNo : dto.getMember_no()) {
					MeetingRoomReservationMapping mapping = MeetingRoomReservationMapping.builder()
							.reservationNo(MeetingRoomReservation.builder().reservationNo(reservationNo).build())
							.memberNo(Member.builder().memberNo(memberNo).build())
							.build();
					
					mappingRepository.save(mapping);
				}
			
				
				result++;
			}
			
		}catch(Exception e ) {
			e.printStackTrace();
		}

		return result;

	}

	
	
	/////////////////////////////////////////////////
	
	// 관리자 - 회의실 예약 내역 전체 조회
	public List<MeetingRoomReservation> adminSelectMeetingReservationAll(){
		return reservationRepositoty.findAll();
	}
	
	
	// 관리자 - 회의실 목록 전체 조회
	public List<MeetingRoom> adminSelectMeetingRoomAll(){
		return repository.findAll();
	}
	
	// 관리자 - 회의실 등룍
	public int adminCreateMeetingRoom(MeetingRoomDto dto) {
		int result = 0;
		
		try {
			MeetingRoom entity = dto.toEntity();
			repository.save(entity);
			result = 1;
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	// 관리자 - 회의실 삭제
	public int adminDeleteMeetingRoom(Long id) {
		int result = 0;
		try {
			MeetingRoom target = repository.findById(id).orElse(null);
			
			if(target != null) {
				MeetingRoomDto dto = new MeetingRoomDto().toDto(target);
				dto.setMeeting_room_status("D");
				repository.save(dto.toEntity());
				result = 1;
			}			
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 관리자 - 회의실 상태 변경
	public int adminUpdateMeetingRoomStatus(Long id) {
		int result = 0;
		try {
			MeetingRoom target = repository.findById(id).orElse(null);
			
			if(target != null) {
				MeetingRoomDto dto = new MeetingRoomDto().toDto(target);
				if(dto.getMeeting_room_status().equals("Y")) {
					dto.setMeeting_room_status("N");
				}else if(dto.getMeeting_room_status().equals("N")) {
					dto.setMeeting_room_status("Y");
				}
				repository.save(dto.toEntity());
				result = 1;
			}			
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return result;
	}

	// 관리자 - 회의실 수정
	public int adminUpdateMeetingRoom(MeetingRoomDto dto) {
		int result = 0;
		try {
			MeetingRoom target = repository.findById(dto.getMeeting_room_no()).orElse(null);
			
			if(target != null) {
				MeetingRoomDto meetingRoomDto = new MeetingRoomDto().toDto(target);
				meetingRoomDto.setMeeting_room_name(dto.getNew_meeting_room_name());
				repository.save(meetingRoomDto.toEntity());
				result = 1;
			}			
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
	
	
}

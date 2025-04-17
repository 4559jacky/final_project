package com.mjc.groupware.meetingRoomReservation.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import com.mjc.groupware.meetingRoomReservation.dto.MeetingRoomDto;
import com.mjc.groupware.meetingRoomReservation.dto.MeetingRoomReservationDto;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoom;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservation;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservationMapping;
import com.mjc.groupware.meetingRoomReservation.repository.MeetingRoomRepository;
import com.mjc.groupware.meetingRoomReservation.repository.MeetingRoomReservationMappingRepository;
import com.mjc.groupware.meetingRoomReservation.repository.MeetingRoomReservationRepository;
import com.mjc.groupware.meetingRoomReservation.specification.MeetingRoomReservationSpecification;
import com.mjc.groupware.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {

	private final MeetingRoomRepository repository;
	private final MeetingRoomReservationRepository reservationRepositoty;
	private final MeetingRoomReservationMappingRepository mappingRepository;
	
	// 사용자, 관리자 - 회의실 예약 내역 전체 조회 
	public List<MeetingRoomReservationDto> selectMeetingRoomReservationAll(MeetingRoomReservationDto filterDto) {
	    // 전체 조회 필터
		Specification<MeetingRoomReservation> spec = (root, query, criteriaBuilder) -> null;
		if (filterDto.getMeeting_room_no() != null) {
	        spec = spec.and(MeetingRoomReservationSpecification
	                .meetingReservationContainsMeeitngRoomNo(filterDto.getMeeting_room_no()));
	    }
	    if (filterDto.getMeeting_date() != null) {
	        spec = spec.and(MeetingRoomReservationSpecification
	                .meetingReservationContainsMeeitngDate(filterDto.getMeeting_date()));
	    }
		 
		// 모든 예약을 조회
	    List<MeetingRoomReservation> reservations = reservationRepositoty.findAll(spec);

	    // 결과를 저장할 DTO 리스트
	    List<MeetingRoomReservationDto> dtoList = new ArrayList<>();

	    // 예약들을 그룹화하기 위한 Map 선언
	    Map<String, List<MeetingRoomReservation>> groupedMap = new LinkedHashMap<>();

	    // 예약들을 그룹화
	    for (MeetingRoomReservation res : reservations) {
	    	// key = 회의실 번호_날짜_제목
	        String key = res.getMeetingRoomNo().getMeetingRoomNo() + "_" +
	                     res.getMeetingDate() + "_" +
	                     res.getMeetingTitle();
	        // 해당 key로 그룹이 없다면 새로 만들고 현재 예약 추가 
	        groupedMap.computeIfAbsent(key, k -> new ArrayList<>()).add(res);
	    }

	    // 그룹화된 예약들을 DTO로 변환
	    for (List<MeetingRoomReservation> group : groupedMap.values()) {
	    	// 첫번째 예약 가져옴 (회의실명, 제목, 날짜 동일)
	        MeetingRoomReservation first = group.get(0);
	        // 첫번째 예약에서 공통 데이터 뽑기 (회의실 명, 번호, 제목 , 날짜)
	        String roomName = first.getMeetingRoomNo().getMeetingRoomName(); 
	        Long meetingRoomNo = first.getMeetingRoomNo().getMeetingRoomNo(); 
	        String title = first.getMeetingTitle();
	        LocalDate date = first.getMeetingDate();
	        String reservationStatus = first.getReservationStatus();
	        Long reservationNo = first.getReservationNo();

	        // 시작 시간과, 참석자 번호, 참석자 이름, 참석자 직급 담을 리스트 만들기 
	        List<LocalTime> startTimes = new ArrayList<>();
	        List<Long> memberNos = new ArrayList<>();
	        List<String> memberNames = new ArrayList<>();

	        // 예약들에서 시작 시간 리스트에 추가 
	        for (MeetingRoomReservation r : group) {
	            startTimes.add(r.getMeetingStartTime());

	            // 예약 하나에 연결된 참석자 정보 조회 
	            // MeetingRoomReservationMapping은 별도로 조회해서 처리
	            List<MeetingRoomReservationMapping> mappings = mappingRepository
	                    .findByReservationNo(r); // 별도로 Repository에서 조회
	            
	            // 참석자 하나씩 돌면서 중복없이 리스트에 넣음
	            for (MeetingRoomReservationMapping rm : mappings) {
	            	Member member = rm.getMemberNo(); 
            	    Long memberNo = member.getMemberNo();
            	    String memberName = member.getMemberName();
            	    
	                if (!memberNos.contains(memberNo)) {
	                    memberNos.add(memberNo);
	                    memberNames.add(memberName);
	                    
	                }
	            }
	        }

	        // 지금까지 모은 정보들 DTO 생성 (회의실 번호, 제목, 날짜, 시간, 참석자, 회의실명)
	        MeetingRoomReservationDto dto = MeetingRoomReservationDto.builder()
	                .meeting_room_no(meetingRoomNo)
	                .meeting_title(title)
	                .meeting_date(date)
	                .meeting_start_time(startTimes)
	                .member_no(memberNos)
	                .member_name(memberNames)
	                .meeting_room_name(roomName)
	                .reservation_status(reservationStatus)
	                .reservation_no(reservationNo)
	                .build();
	        // 최종 리스트레 추가 
	        dtoList.add(dto);
	    }

	    return dtoList;
	}


	//////////////////////////////////////////////////////////////////////////////////////
	
	
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
						.reservationStatus(dto.getReservation_status())
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

	// 사용자 - 회의실 예약 취소 
	public int deleteReservation(Long reservationNo) {
	    int result = 0;
	    try {
	        // 예약 가져오기
	        MeetingRoomReservation target = reservationRepositoty.findById(reservationNo).orElse(null);

	        if (target != null) {
	            // 상태가 Y면 N으로, N이면 Y로 바꾸기 (취소 기능 기준으로는 그냥 N으로 바꿔도 됨)
	            if ("Y".equals(target.getReservationStatus())) {
	                target.setReservationStatus("N");;  // 취소된 상태
	                reservationRepositoty.save(target);
	                result = 1;
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	
	////////////////////////////////////////////////////////////////////
	
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

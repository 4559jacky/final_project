package com.mjc.groupware.accommodationReservation.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.accommodationReservation.dto.AccommodationReservationDto;
import com.mjc.groupware.accommodationReservation.service.AccommodationReservationService;
import com.mjc.groupware.board.controller.BoardAttachController;
import com.mjc.groupware.member.security.MemberDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccommodationReservationController {

    private final BoardAttachController boardAttachController;

	private final AccommodationReservationService reservationService;

    // 예약 등록
	@PostMapping("/user/accommodation/reservation/create")
	@ResponseBody
	public Map<String, Object> createReservation(@ModelAttribute AccommodationReservationDto dto,
	                                             @AuthenticationPrincipal MemberDetails memberDetails) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        Long memberNo = memberDetails.getMember().getMemberNo();
	        dto.setMember_no(memberNo);
	        dto.setReservation_date(LocalDateTime.now());
	        dto.setReservation_status("대기");

	        reservationService.createReservation(dto);

	        result.put("res_code", "200");
	        result.put("res_msg", "예약이 완료되었습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        result.put("res_code", "500");
	        result.put("res_msg", "예약에 실패하였습니다.");
	    }
	    return result;
	}

    // 사용자 예약 내역
	@GetMapping("/user/accommodation/reservation/list")
	public String userReservationList(Model model,
	                                  @AuthenticationPrincipal MemberDetails memberDetails) {
	    Long memberId = memberDetails.getMember().getMemberNo();
	    List<AccommodationReservationDto> list = reservationService.getReservationsByMember(memberId);
	    model.addAttribute("reservationList", list);
	    return "accommodation/list";
	}

    // 관리자 예약 현황
    @GetMapping("/admin/accommodation/reservation/list")
    public String adminReservationList(@RequestParam("accommodation_no") Long accommodationNo, Model model) {
        List<AccommodationReservationDto> list = reservationService.getReservationsByAccommodation(accommodationNo);
        model.addAttribute("reservationList", list);
        return "accommodation/adminList";
    }
    
    // 예약 상태변경 API
    @PostMapping("/admin/accommodation/reservation/updateStatus")
    @ResponseBody
    public Map<String, Object> updateReservationStatus(@RequestParam("reservationNo") Long reservationNo,
                                                       @RequestParam("status") int status,
                                                       @RequestParam(value = "rejectReason", required = false) String rejectReason) {
        Map<String, Object> result = new HashMap<>();
        try {
            reservationService.updateReservationStatus(reservationNo, status, rejectReason);
            result.put("res_code", "200");
            result.put("res_msg", "상태가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("res_code", "500");
            result.put("res_msg", "예약 상태 변경 실패: " + e.getMessage());
        }
        return result;
    }

	
}

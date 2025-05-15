package com.mjc.groupware.accommodationReservation.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mjc.groupware.accommodationReservation.dto.AccommodationInfoDto;
import com.mjc.groupware.accommodationReservation.dto.ApproveResponse;
import com.mjc.groupware.accommodationReservation.dto.ReadyResponse;
import com.mjc.groupware.accommodationReservation.service.KakaoPayService;
import com.mjc.groupware.common.util.SessionUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    @PostMapping("/pay/ready")
    public @ResponseBody ReadyResponse payReady(@RequestBody AccommodationInfoDto dto) {
        
        String name = dto.getAccommodation_name();
        Long totalPrice = dto.getRoom_price();
        System.out.println("test : "+name);
        System.out.println("test : "+totalPrice);
        // 카카오 결제 준비하기
        ReadyResponse readyResponse = kakaoPayService.payReady(name, totalPrice);
        // 세션에 결제 고유번호(tid) 저장
        SessionUtils.addAttribute("tid", readyResponse.getTid());

        return readyResponse;
    }

    @GetMapping("/pay/completed")
    public String payCompleted(@RequestParam("pg_token") String pgToken) {
    
        String tid = SessionUtils.getStringAttributeValue("tid");

        // 카카오 결제 요청하기
        ApproveResponse approveResponse = kakaoPayService.payApprove(tid, pgToken);

        return "redirect:/order/completed";
    }
    
    
}


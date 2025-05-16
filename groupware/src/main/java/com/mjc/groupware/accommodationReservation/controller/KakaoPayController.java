package com.mjc.groupware.accommodationReservation.controller;


import java.io.IOException;
import java.io.PrintWriter;

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

import jakarta.servlet.http.HttpServletResponse;
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
        System.out.println("결제 고유번호: " + readyResponse.getTid());
        return readyResponse;
    }

    @GetMapping("/pay/completed")
    public void payCompleted(@RequestParam("pg_token") String pgToken, HttpServletResponse response)throws IOException {
    
        String tid = SessionUtils.getStringAttributeValue("tid");
//        log.info("결제승인 요청을 인증하는 토큰: " + pgToken);
//        log.info("결제 고유번호: " + tid);
        
        // 카카오 결제 요청하기
        ApproveResponse approveResponse = kakaoPayService.payApprove(tid, pgToken);
        
        //응답설정
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>결제완료</title>");
        out.println("<script>");
        out.println("window.onload = function() {");
        out.println("    if (window.opener) {");
        out.println("        window.opener.location.href = '/order/completed';");
        out.println("    }");
        out.println("    window.close();");
        out.println("};");
        out.println("</script></head>");
        out.println("<body>");
        out.println("<p>결제가 완료되었습니다. 창이 곧 닫힙니다.</p>");
        out.println("</body></html>");
//        return "redirect:/order/completed";
    }
    
    
}


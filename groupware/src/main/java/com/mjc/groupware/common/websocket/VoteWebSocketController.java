package com.mjc.groupware.common.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class VoteWebSocketController {
	
	@MessageMapping("/vote/register")
    public void registerVoteAlarm(VoteRegisterRequest dto) {
        System.out.println("📩 투표 알림 등록 요청 memberNo = " + dto.getMemberNo());
        // 등록 처리 로직 (필요 시 세션 저장 등)
    }

    // DTO 예시
    public static class VoteRegisterRequest {
        private Long memberNo;
        public Long getMemberNo() { return memberNo; }
        public void setMemberNo(Long memberNo) { this.memberNo = memberNo; }
    }
}

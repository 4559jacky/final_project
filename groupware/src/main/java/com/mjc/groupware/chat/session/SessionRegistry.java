package com.mjc.groupware.chat.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class SessionRegistry { 
	// 해당 멤버가 어떤 세션 ID로 접속했는지 확인하는 클래스
	
	// 멤버 번호 - 세션 ID	저장
    private final Map<Long, String> memberToSession = new ConcurrentHashMap<>();

    // 특정 유저가 웹소캣 연결되면 세션 ID 저장
    public void register(Long memberNo, String sessionId) {
        memberToSession.put(memberNo, sessionId);
    }

    //유저 번호로 어떤 세션 ID를 가졌는지 확인
    public String getSessionIdForMember(Long memberNo) {
        return memberToSession.get(memberNo);
    }

    // 세션 끊겼을 때 모든 유저의 세션 중 이 세션 ID를 가진 유저를 제거
    public void unregister(String sessionId) {
        memberToSession.values().removeIf(v -> v.equals(sessionId));
    }
}


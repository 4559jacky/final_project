package com.mjc.groupware.chat.session;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ChatSessionTracker {
	// 세션  ID 가 지금 어떤 채팅방에 들어가있는지 확인 
	

	// 채팅방 번호 - 그 방에 접속한 세션  ID 목록
	private final Map<Long, Set<String>> roomToSessions = new ConcurrentHashMap<>();

	// 누가 채팅방에 들어갔을 떄 호출
    public void enterRoom(Long roomNo, String sessionId) {
        roomToSessions.computeIfAbsent(roomNo, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    // 이 세션ID가 특정 방 안에 잇는 지 확인 
    public boolean isSessionInRoom(Long roomNo, String sessionId) {
        return roomToSessions.getOrDefault(roomNo, Set.of()).contains(sessionId);
    }

    // 특정 방에 현재 접속 중인 세션 ID 목록을 반환
    public Set<String> getSessionsInRoom(Long roomNo) {
        return roomToSessions.getOrDefault(roomNo, Set.of());
    }

    // 누가 웹소캣 연결 끊으면 해당 세션들을 모든 방에서 제거 
    public void removeSession(String sessionId) {
        for (Set<String> sessions : roomToSessions.values()) {
            sessions.remove(sessionId);
        }
    }
}


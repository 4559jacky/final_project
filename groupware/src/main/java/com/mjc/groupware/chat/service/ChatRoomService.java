package com.mjc.groupware.chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.chat.dto.ChatMappingDto;
import com.mjc.groupware.chat.dto.ChatRoomDto;
import com.mjc.groupware.chat.dto.ChatRoomReadDto;
import com.mjc.groupware.chat.entity.ChatMapping;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.entity.ChatRoomRead;
import com.mjc.groupware.chat.repository.ChatMappingrepository;
import com.mjc.groupware.chat.repository.ChatMsgRepository;
import com.mjc.groupware.chat.repository.ChatRoomReadRepository;
import com.mjc.groupware.chat.repository.ChatRoomRepository;
import com.mjc.groupware.chat.specification.ChatMappingSpecification;
import com.mjc.groupware.chat.specification.ChatRoomReadSpecification;
import com.mjc.groupware.chat.specification.ChatRoomSpecification;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.security.MemberDetails;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
	
	private final ChatRoomRepository chatRoomRepository;
	private final ChatMappingrepository mappingRepository;
	private final ChatMsgRepository msgRepository;
	private final ChatRoomReadRepository readRepository;
	
	 @PersistenceContext
	private EntityManager entityManager;
	
	// 채팅 화면 전환
	 public List<ChatRoomDto> selectChatRoomAll() {

		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    MemberDetails md = (MemberDetails) authentication.getPrincipal();

		    Specification<ChatRoom> spec = ChatRoomSpecification.participatedBy(md.getMember());
		    Sort sort = Sort.by(Sort.Direction.DESC, "lastMsgDate");

		    List<ChatRoom> list = chatRoomRepository.findAll(spec, sort);
		    List<ChatRoomDto> result = new ArrayList<>();

		    for (ChatRoom room : list) {
		        String title = room.getChatRoomTitle();

		        if (title.trim().isEmpty()) {
		            List<String> nameList = new ArrayList<>();

		            for (ChatMapping mapping : room.getMappings()) {
		                Member member = mapping.getMemberNo();

		                // ✅ 본인 제외 + 참여중인 멤버만
		                if (!mapping.getMemberNo().getMemberNo().equals(md.getMember().getMemberNo())
		                	    && "Y".equals(mapping.getMemberStatus())) {
		                    String pos = member.getPos() != null ? member.getPos().getPosName() : "";
		                    nameList.add(member.getMemberName() + " " + pos);
		                }
		            }

		            title = String.join(", ", nameList);
		        }

		        ChatRoomDto dto = ChatRoomDto.builder()
		                .chat_room_no(room.getChatRoomNo())
		                .chat_room_title(title)
		                .last_msg(room.getLastMsg())
		                .last_msg_date(room.getLastMsgDate())
		                .build();

		        result.add(dto);
		    }

		    return result;
		}

	
	// 채팅방 생성
	@Transactional
	public ChatRoom createChatRoom(ChatRoomDto dto) {
	    ChatRoom chatroom = ChatRoom.builder()
	        .chatRoomTitle(dto.getChat_room_title())
	        .createMemberNo(Member.builder().memberNo(dto.getCreate_member_no()).build())
	        .build();

	    ChatRoom saved = chatRoomRepository.save(chatroom);

	    for (Long memberNo : dto.getMember_no()) {
	        ChatMapping mappings = ChatMapping.builder()
	            .chatRoomNo(saved)
	            .memberNo(Member.builder().memberNo(memberNo).build())
	            .build();
	        mappingRepository.save(mappings);
	    }

	    // 💥 여기 추가
	    entityManager.flush();
	    entityManager.clear();

	    return saved;
	}

	// 채팅방 상세 조회
	public ChatRoom selectChatRoomOne(Long chatRoomNo) {
	    ChatRoom room = chatRoomRepository.findById(chatRoomNo).orElse(null);

	    if (room != null) {
	        List<ChatMapping> originList = room.getMappings(); // 원래 전체 맵핑 리스트
	        List<ChatMapping> filteredList = new ArrayList<>();

	        for (ChatMapping mapping : originList) {
	            if ("Y".equals(mapping.getMemberStatus())) {
	                filteredList.add(mapping);
	            }
	        }

	        room.setMappings(filteredList); // ✅ 필터링된 리스트로 덮어쓰기
	    }

	    return room;
	}

	
	// 읽음 시간 조회 
	public int selectUnreadMsgCount(ChatRoomReadDto dto) {
	    // 1. 로그인한 사용자 가져오기
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    MemberDetails md = (MemberDetails) authentication.getPrincipal();
	    Member loginUser = md.getMember();

	    // 2. 읽음 시간 조회
	    dto.setMember_no(loginUser.getMemberNo());
	    Specification<ChatRoomRead> spec = ChatRoomReadSpecification.ChatRoomNoEqualsAndMemberNoEquals(dto);
	    List<ChatRoomRead> readList = readRepository.findAll(spec);

	    LocalDateTime lastReadTime = readList.isEmpty()
	        ? LocalDateTime.MIN
	        : readList.get(0).getLastReadTime();

	    // 3. 채팅방 조회
	    ChatRoom chatRoom = chatRoomRepository.findById(dto.getChat_room_no())
	        .orElseThrow(() -> new RuntimeException("채팅방 없음"));

	    // 4. 내가 보낸 메시지 제외하고, 안읽은 메시지 개수 반환
	    return msgRepository.countByChatRoomNoAndSendDateAfterAndMemberNoNot(
	        chatRoom, lastReadTime, loginUser
	    );
	}


	// 채팅방 읽음 처리 
	public ChatRoomReadDto updateReadTime(ChatRoomReadDto dto) {
	    Specification<ChatRoomRead> spec = ChatRoomReadSpecification.ChatRoomNoEqualsAndMemberNoEquals(dto);
	    List<ChatRoomRead> list = readRepository.findAll(spec);

	    ChatRoomRead record;

	    if (list.isEmpty()) {
	        record = dto.toEntity(); // ✅ 딱 이 한 줄로 변환
	    } else {
	        record = list.get(0);
	        record.setLastReadTime(LocalDateTime.now()); 
	    }

	    ChatRoomRead saved = readRepository.save(record);
	    return ChatRoomReadDto.toDto(saved); 
	}


	// 채팅방 나가기 
	public int updateStatus(ChatMappingDto dto) {
	    int result = 0;
	    
	    try {
	        Specification<ChatMapping> spec = ChatMappingSpecification.ChatRoomNoEqualsAndMemberNoEquals(dto);
	        List<ChatMapping> list = mappingRepository.findAll(spec);

	       
	        for (ChatMapping mapping : list) {
	            mapping.setMemberStatus("N");
	        }

	        mappingRepository.saveAll(list);
	        result = list.size();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return result;
	}

}

package com.mjc.groupware.attendance.controller;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mjc.groupware.attendance.service.HolidayService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HolidayInit {

    private final HolidayService holidayService;

//    @PostConstruct
//    public void init() {
//        try {
//			holidayService.fetchAndSaveHolidays(2025);
//			System.out.println("실행 test");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} // 연초에 1회만 저장
//    }
    
//    @EventListener(ApplicationReadyEvent.class)
//    public void init() {
//        try {
//            holidayService.fetchAndSaveHolidays(2025);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

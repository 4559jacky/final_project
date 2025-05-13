package com.mjc.groupware.attendance.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mjc.groupware.attendance.entity.Holiday;
import com.mjc.groupware.attendance.repository.HolidayRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${service.key}")
    private String SERVICE_KEY;
    
    @Transactional(rollbackFor = Exception.class)
    public void fetchAndSaveHolidays(int year) throws SAXException, IOException, ParserConfigurationException {
    	String encodedKey = URLEncoder.encode(SERVICE_KEY, StandardCharsets.UTF_8);
    	String url = String.format(
    		    "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo" +
    		    "?ServiceKey=%s&solYear=%d&numOfRows=100", encodedKey, year
    		);
        
        System.out.println("키 테스트 : "+SERVICE_KEY);

        String xml = restTemplate.getForObject(url, String.class);
        
        System.out.println("📦 응답 xml 시작 ====");
        System.out.println(xml);
        System.out.println("📦 응답 xml 끝 ====");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        Document doc = builder.parse(is);

        // 노드 가져오기
        NodeList itemList = doc.getElementsByTagName("item");
        System.out.println("총 공휴일 수: " + itemList.getLength());
        for (int i = 0; i < itemList.getLength(); i++) {
            Element item = (Element) itemList.item(i);

            String dateName = item.getElementsByTagName("dateName").item(0).getTextContent(); // 어린이날
            String locdate = item.getElementsByTagName("locdate").item(0).getTextContent();   // 20250505

            // 형식 변환
            LocalDate date = LocalDate.parse(locdate, DateTimeFormatter.ofPattern("yyyyMMdd"));

            // 저장
            Holiday holiday = Holiday.builder()
                .name(dateName)
                .date(date)
                .build();

            holidayRepository.save(holiday);
        }
    }
}

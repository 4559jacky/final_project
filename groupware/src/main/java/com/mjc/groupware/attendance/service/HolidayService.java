package com.mjc.groupware.attendance.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mjc.groupware.attendance.dto.HolidayDto;
import com.mjc.groupware.attendance.entity.Holiday;
import com.mjc.groupware.attendance.repository.HolidayRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    
    @Value("${service.key}")
    private String SERVICE_KEY;
    
    @Transactional(rollbackFor = Exception.class)
    public void fetchAndSaveHolidays(int year) throws Exception {
        String baseUrl = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
        String fullUrl = String.format(
            "%s?serviceKey=%s&solYear=%d&numOfRows=100",
            baseUrl,
            SERVICE_KEY,
            year
        );

        System.out.println("🌐 요청 URL: " + fullUrl);

        URL url = new URL(fullUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        System.out.println("🔍 응답 코드: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String xml = response.toString();

        System.out.println("📦 응답 xml 시작 ====");
        System.out.println(xml);
        System.out.println("📦 응답 xml 끝 ====");

        // XML 파싱
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        Document doc = builder.parse(is);

        NodeList itemList = doc.getElementsByTagName("item");
        System.out.println("총 공휴일 수: " + itemList.getLength());

        for (int i = 0; i < itemList.getLength(); i++) {
            Element item = (Element) itemList.item(i);

            String dateName = item.getElementsByTagName("dateName").item(0).getTextContent();
            String locdate = item.getElementsByTagName("locdate").item(0).getTextContent();

            LocalDate date = LocalDate.parse(locdate, DateTimeFormatter.ofPattern("yyyyMMdd"));

            Holiday holiday = Holiday.builder()
                .name(dateName)
                .date(date)
                .build();

            holidayRepository.save(holiday);
        }
    }
    
    @Transactional(rollbackFor=Exception.class)
    public Map<String, Object> holidayCreateApi(HolidayDto holidayDto) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            // 중복 체크
            boolean exists = holidayRepository.existsByNameAndDate(
                holidayDto.getHoliday_name(), holidayDto.getHoliday_date());

            if (exists) {
                resultMap.put("res_code", "409"); // Conflict
                resultMap.put("res_msg", "같은 이름과 날짜의 휴일이 이미 존재합니다.");
                return resultMap;
            }

            Holiday holidayEntity = holidayDto.toEntity();
            holidayRepository.save(holidayEntity);

            List<Holiday> holidayList = holidayRepository.findAllByOrderByDateAsc();
            resultMap.put("holidayList", holidayList);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "휴일 정보를 추가하였습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "휴일 정보 추가 중 오류가 발생하였습니다.");
        }

        return resultMap;
    }

    @Transactional(rollbackFor=Exception.class)
	public Map<String, Object> holidayUpdateApi(HolidayDto holidayDto) {
    	Map<String, Object> resultMap = new HashMap<>();
        try {
            // 중복 체크
            boolean exists = holidayRepository.existsByNameAndDate(
                holidayDto.getHoliday_name(), holidayDto.getHoliday_date());

            if (exists) {
            	List<Holiday> holidayList = holidayRepository.findAllByOrderByDateAsc();
                resultMap.put("holidayList", holidayList);
                resultMap.put("res_code", "409"); // Conflict
                resultMap.put("res_msg", "같은 이름과 날짜의 휴일이 이미 존재합니다.");
                return resultMap;
            }

            Holiday holidayEntity = holidayDto.toEntity();
            holidayRepository.save(holidayEntity);

            List<Holiday> holidayList = holidayRepository.findAllByOrderByDateAsc();
            resultMap.put("holidayList", holidayList);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "휴일 정보를 수정하였습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "휴일 정보 수정 중 오류가 발생하였습니다.");
        }

        return resultMap;
	}
    
    @Transactional(rollbackFor=Exception.class)
	public Map<String, Object> holidayDeleteApi(Long id) {
		Map<String, Object> resultMap = new HashMap<>();
        try {

            Holiday holidayEntity = holidayRepository.findById(id).orElse(null);
            holidayRepository.delete(holidayEntity);

            List<Holiday> holidayList = holidayRepository.findAllByOrderByDateAsc();
            resultMap.put("holidayList", holidayList);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "휴일 정보를 삭제하였습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "휴일 정보 삭제 중 오류가 발생하였습니다.");
        }

        return resultMap;
	}
}

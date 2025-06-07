package com.mjc.groupware.attendance.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
}

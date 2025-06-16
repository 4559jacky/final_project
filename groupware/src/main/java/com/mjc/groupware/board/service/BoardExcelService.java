//package com.mjc.groupware.board.service;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.List;
//
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Service;
//
//import com.mjc.groupware.board.dto.BoardDto;
//import com.mjc.groupware.board.dto.PageDto;
//import com.mjc.groupware.board.dto.SearchDto;
//import com.mjc.groupware.board.entity.Board;
//
//// 게시판안에 아파치 포이 추가
//@Service
//public class BoardExcelService {
//
//    public ByteArrayInputStream createExcel(List<BoardDto> boardList) throws IOException {
//        try (Workbook workbook = new XSSFWorkbook()) {
//            Sheet sheet = workbook.createSheet("게시판 목록");
//
//            Row headerRow = sheet.createRow(0);
//            String[] headers = {"번호", "제목", "작성자", "등록일", "조회수"};
//            for (int i = 0; i < headers.length; i++) {
//                headerRow.createCell(i).setCellValue(headers[i]);
//            }
//
//            int rowNum = 1;
//            for (BoardDto board : boardList) {
//                Row row = sheet.createRow(rowNum++);
//                row.createCell(0).setCellValue(board.getBoard_no());
//                row.createCell(1).setCellValue(board.getBoard_title());
//                row.createCell(2).setCellValue(board.getMember_name());
//                row.createCell(3).setCellValue(board.getReg_date().toString());
//                row.createCell(4).setCellValue(board.getViews());
//            }
//
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            workbook.write(out);
//            return new ByteArrayInputStream(out.toByteArray());
//        }
//    }
//    
//    public List<BoardDto> selectBoardDtoList(SearchDto searchDto, PageDto pageDto) {
//        Page<Board> boards = selectBoardAll(searchDto, pageDto);
//        return boards.getContent().stream()
//            .map(BoardDto::fromEntity)
//            .toList();
//    }
//}
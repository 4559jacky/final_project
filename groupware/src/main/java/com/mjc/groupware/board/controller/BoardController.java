package com.mjc.groupware.board.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.service.BoardService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시판 목록
    @GetMapping("/board/list")
    public String selectBoardList(Model model) {
        List<BoardDto> boardList = boardService.selectBoardList();
        model.addAttribute("boardList", boardList);
        return "board/list"; // 목록 HTML
    }
    // 게시판 작성
    @GetMapping("/board/create")
    public String createForm(Model model) {
        model.addAttribute("boardDTO", new BoardDto());
        return "board/create"; // create.html
    }

    // 작성 처리
    @PostMapping("/board/create")
    public String createSubmit(@ModelAttribute BoardDto boardDTO) {
        boardService.createBoard(boardDTO);
        return "redirect:/board/list";
    }
}
package com.mjc.groupware.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.service.BoardService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 게시판 목록
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("boards", boardService.getBoardList());
        return "board/list"; // list.html
    }

    // 게시판 작성
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("boardDTO", new BoardDto());
        return "board/create"; // create.html
    }

    // 작성 처리
    @PostMapping("/create")
    public String createSubmit(@ModelAttribute BoardDto boardDTO) {
        boardService.createBoard(boardDTO);
        return "redirect:/board/list";
    }
}
package com.jihun.mysimpleblog.board.controller.view;

import com.jihun.mysimpleblog.board.service.CommentService;
import com.jihun.mysimpleblog.board.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardViewController {
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping
    public String boards() {
        return "board/boards";
    }




}

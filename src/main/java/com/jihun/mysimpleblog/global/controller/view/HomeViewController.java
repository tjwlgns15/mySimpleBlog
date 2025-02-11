package com.jihun.mysimpleblog.global.controller.view;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.entity.dto.UserResponse;
import com.jihun.mysimpleblog.auth.service.UserService;
import com.jihun.mysimpleblog.board.entity.dto.post.PostResponse;
import com.jihun.mysimpleblog.board.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class HomeViewController {

    private final UserService userService;
    private final PostService postService;


    @GetMapping("/home")
    public String home() {
        return "home";
    }

}

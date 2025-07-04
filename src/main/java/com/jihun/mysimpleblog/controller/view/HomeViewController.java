package com.jihun.mysimpleblog.controller.view;

import com.jihun.mysimpleblog.service.UserService;
import com.jihun.mysimpleblog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class HomeViewController {

    @GetMapping("/home")
    @PreAuthorize("hasRole('GUEST')")
    public String home() {
        return "home";
    }

}

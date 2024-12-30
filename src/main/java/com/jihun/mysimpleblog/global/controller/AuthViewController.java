package com.jihun.mysimpleblog.global.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class AuthViewController {

    @GetMapping("/user")
    public String user() {
        return "auth/user";
    }

    @GetMapping("/admin")
    public String admin() {
        return "auth/admin";
    }

}

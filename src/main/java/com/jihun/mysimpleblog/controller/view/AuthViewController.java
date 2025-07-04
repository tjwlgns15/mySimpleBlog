package com.jihun.mysimpleblog.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthViewController {

    @GetMapping("/auth/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/auth/signup")
    public String signup() {
        return "auth/signup";
    }

    @GetMapping("/user")
    public String user() {
        return "auth/user";
    }

    @GetMapping("/my-page")
    public String myPage() {
        return "auth/my-page";
    }

}

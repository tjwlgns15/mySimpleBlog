package com.jihun.mysimpleblog.auth.controller.view;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
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
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        return "auth/my-page";
    }

}

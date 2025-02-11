package com.jihun.mysimpleblog.auth.controller.view;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping
    public String admin() {
        return "auth/admin/dashboard";  // /admin으로 접속하면 바로 dashboard.html 렌더링
    }

    @GetMapping("/users")
    public String users() {
        return "auth/admin/management-users";
    }
}
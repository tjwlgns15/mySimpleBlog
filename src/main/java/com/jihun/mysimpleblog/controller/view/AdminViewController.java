package com.jihun.mysimpleblog.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping("/dashboard")
    public String admin() {
        return "auth/admin/dashboard";
    }

    @GetMapping("/users")
    public String users() {
        return "auth/admin/management-users";
    }
}
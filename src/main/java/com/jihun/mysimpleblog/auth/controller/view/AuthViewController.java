package com.jihun.mysimpleblog.auth.controller.view;

import com.jihun.mysimpleblog.auth.config.core.CustomUserDetails;
import com.jihun.mysimpleblog.auth.entity.User;
import com.jihun.mysimpleblog.auth.entity.dto.UserResponse;
import com.jihun.mysimpleblog.auth.service.UserService;
import com.jihun.mysimpleblog.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthViewController {

    private final UserService userService;


    @GetMapping("/user")
    public String user() {
        return "auth/user";
    }

    @GetMapping("/admin")
    public String admin() {
        return "auth/admin";
    }

    @GetMapping("/my-page")
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            // User를 ProfileImage와 함께 조회
            UserResponse response = userService.findByIdWithProfileImage(userDetails.getUser().getId());
            model.addAttribute("user", response);
        }
        return "auth/my-page";
    }

}

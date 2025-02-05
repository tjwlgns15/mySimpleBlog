package com.jihun.mysimpleblog.auth.entity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequest {
    private String name;
    private String introduction;
}
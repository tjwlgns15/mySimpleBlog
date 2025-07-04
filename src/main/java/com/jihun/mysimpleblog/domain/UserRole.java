package com.jihun.mysimpleblog.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN", 1),
    MANAGER("ROLE_MANAGER", 2),
    USER("ROLE_USER", 3),
    GUEST("ROLE_GUEST", 4);

    private final String roleName;
    private final int level;

    UserRole(String roleName, int level) {
        this.roleName = roleName;
        this.level = level;
    }

    public static String buildHierarchy() {
        UserRole[] roles = UserRole.values();

        // 레벨 기준으로 내림차순 정렬 (관리자가 제일 높음)
        Arrays.sort(roles, (r1, r2) -> Integer.compare(r1.level, r2.level));

        StringBuilder hierarchy = new StringBuilder();

        for (int i = 0; i < roles.length - 1; i++) {
            UserRole higher = roles[i];
            UserRole lower = roles[i + 1];

            hierarchy.append(higher.getRoleName())
                    .append(" > ")
                    .append(lower.getRoleName())
                    .append("\n");
        }

        return hierarchy.toString();
    }

    public List<UserRole> getSubordinateRoles() {
        return Arrays.stream(UserRole.values())
                .filter(role -> role.level > this.level)
                .collect(Collectors.toList());
    }

    public List<UserRole> getSuperiorRoles() {
        return Arrays.stream(UserRole.values())
                .filter(role -> role.level < this.level)
                .collect(Collectors.toList());
    }

    public boolean isHigherThan(UserRole other) {
        return this.level < other.level;
    }

    public boolean isLowerThan(UserRole other) {
        return this.level > other.level;
    }
}

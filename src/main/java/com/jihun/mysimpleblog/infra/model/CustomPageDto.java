package com.jihun.mysimpleblog.infra.model;

import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public class CustomPageDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomPageRequest {
        private int page = 0;
        private int size = 10;
        private String sort;
        private String direction;

        public PageRequest toPageRequest() {
            if (sort != null && !sort.isEmpty()) {
                Sort.Direction sortDirection = direction != null && direction.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
                return PageRequest.of(page, size, Sort.by(sortDirection, sort));
            }
            return PageRequest.of(page, size);
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomPageResponse<T> {
        private List<T> content;
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;

        public static <T> CustomPageResponse<T> fromPage(Page<T> page) {
            return CustomPageResponse.<T>builder()
                    .content(page.getContent())
                    .pageNumber(page.getNumber())
                    .pageSize(page.getSize())
                    .totalElements(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .first(page.isFirst())
                    .last(page.isLast())
                    .build();
        }

    }
}

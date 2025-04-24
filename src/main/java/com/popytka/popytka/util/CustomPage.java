package com.popytka.popytka.util;

import org.springframework.data.domain.Page;

import java.util.List;

public record CustomPage<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages
) {
    public CustomPage(Page<T> page) {
        this(
                page.getContent(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}

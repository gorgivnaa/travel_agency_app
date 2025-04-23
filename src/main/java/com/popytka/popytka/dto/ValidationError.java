package com.popytka.popytka.dto;

public record ValidationError(
        String field,
        String message
) { }

package com.frame.base.dto;

public record ResponseDto(
        String code,
        String message,
        Object result
) {
}

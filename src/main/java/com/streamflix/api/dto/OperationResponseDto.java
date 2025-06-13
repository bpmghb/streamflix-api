package com.streamflix.api.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta de operação simples
 */
public record OperationResponseDto(
        boolean success,
        String message,
        LocalDateTime timestamp
) {
    public static OperationResponseDto success(String message) {
        return new OperationResponseDto(true, message, LocalDateTime.now());
    }

    public static OperationResponseDto error(String message) {
        return new OperationResponseDto(false, message, LocalDateTime.now());
    }
}

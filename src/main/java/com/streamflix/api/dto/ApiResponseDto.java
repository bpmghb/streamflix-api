package com.streamflix.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * DTO genérico para respostas de API
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseDto<T>(
        boolean success,
        String message,
        T data,
        LocalDateTime timestamp
) {
    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(true, "Operação realizada com sucesso", data, LocalDateTime.now());
    }

    public static <T> ApiResponseDto<T> success(String message, T data) {
        return new ApiResponseDto<>(true, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponseDto<T> error(String message) {
        return new ApiResponseDto<>(false, message, null, LocalDateTime.now());
    }
}

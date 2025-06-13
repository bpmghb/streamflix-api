package com.streamflix.api.dto;

import java.util.List;

/**
 * DTO para paginação
 */
public record PageResponseDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {}


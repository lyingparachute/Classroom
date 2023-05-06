package com.example.classroom.pageable;

import lombok.Builder;

@Builder
public record PageableRequest(
        String name,
        int pageNumber,
        int pageSize,
        String sortField,
        String sortDir
) {
}

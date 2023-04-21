package com.example.classroom.pageable;

import org.springframework.data.domain.Page;

public class PageableService {
    private PageableService() {
    }

    public static long getFirstItemOnPage(Page<?> page, int pageNum, int pageSize) {
        return Math.min((long) pageSize * (pageNum - 1) + 1, page.getTotalElements());
    }

    public static long getLastItemOnPage(Page<?> page, int pageNum, int pageSize) {
        return Math.min((long) pageSize * pageNum, page.getTotalElements());
    }
}

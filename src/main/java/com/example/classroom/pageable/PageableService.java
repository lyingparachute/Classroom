package com.example.classroom.pageable;

import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

public class PageableService {

    private PageableService() {
    }

    public static long getFirstItemOnPage(Page<?> page, int pageNum, int pageSize) {
        return Math.min((long) pageSize * (pageNum - 1) + 1, page.getTotalElements());
    }

    public static long getLastItemOnPage(Page<?> page, int pageNum, int pageSize) {
        return Math.min((long) pageSize * pageNum, page.getTotalElements());
    }

    public static Map<String, ?> getAttributesForPageable(Page<?> page,
                                                          PageableRequest request) {
        Map<String, java.io.Serializable> attributes = new HashMap<>();
        attributes.put("currentPage", page.getNumber() + 1);
        attributes.put("totalPages", page.getTotalPages());
        attributes.put("totalItems", page.getTotalElements());
        attributes.put("pageSize", request.pageSize());
        attributes.put("sortField", request.sortField());
        attributes.put("sortDir", request.sortDir());
        attributes.put("reverseSortDir", request.sortDir().equals("asc") ? "desc" : "asc");
        attributes.put("firstItemShownOnPage", getFirstItemOnPage(page, request.pageNumber(), request.pageSize()));
        attributes.put("lastItemShownOnPage", getLastItemOnPage(page, request.pageNumber(), request.pageSize()));
        if (request.name() != null || !request.name().isBlank())
            attributes.put("name", request.name());
        return attributes;
    }
}

package com.example.classroom.pageable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageableService {

    public static long getFirstItemOnPage(final Page<?> page, final int pageNum, final int pageSize) {
        return Math.min((long) pageSize * (pageNum - 1) + 1, page.getTotalElements());
    }

    public static long getLastItemOnPage(final Page<?> page, final int pageNum, final int pageSize) {
        return Math.min((long) pageSize * pageNum, page.getTotalElements());
    }

    public static Map<String, ?> getAttributesForPageable(final Page<?> page,
                                                          final PageableRequest request) {
        final Map<String, java.io.Serializable> attributes = new HashMap<>();
        attributes.put("currentPage", page.getNumber() + 1);
        attributes.put("totalPages", page.getTotalPages());
        attributes.put("totalItems", page.getTotalElements());
        attributes.put("pageSize", request.pageSize());
        attributes.put("sortField", request.sortField());
        attributes.put("sortDir", request.sortDirection());
        attributes.put("reverseSortDir", request.sortDirection().equals("asc") ? "desc" : "asc");
        attributes.put("firstItemShownOnPage", getFirstItemOnPage(page, request.pageNumber(), request.pageSize()));
        attributes.put("lastItemShownOnPage", getLastItemOnPage(page, request.pageNumber(), request.pageSize()));
        if (isNamePresent(request.searched()))
            attributes.put("name", request.searched());
        return attributes;
    }

    public static boolean isNamePresent(final String name) {
        return !(name == null || name.isBlank());
    }
}

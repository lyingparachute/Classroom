package com.example.classroom.breadcrumb;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BreadcrumbService {

    public List<Breadcrumb> getBreadcrumbs(String endpoint) {
        List<Breadcrumb> crumbs = new ArrayList<>();
        crumbs.add(new Breadcrumb("Classroom", "/"));

        String[] segments = splitEndpoint(endpoint);
        StringBuilder endpointBuilder = new StringBuilder("/");

        for (String segment : segments) {
            if (!segment.isEmpty()) {
                endpointBuilder.append(segment).append("/");
                crumbs.add(new Breadcrumb(capitalize(segment), endpointBuilder.toString()));
            }
        }
        if (!crumbs.isEmpty()) {
            Breadcrumb last = crumbs.get(crumbs.size() - 1);
            last.setLast(true);
        }
        return crumbs;
    }

    private String[] splitEndpoint(String endpoint) {
        return endpoint.split("/");
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}

package com.example.classroom.breadcrumb;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BreadcrumbService {

    public List<Breadcrumb> getBreadcrumbs(String endpoint) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(homeEndpoint());

        String[] segments = splitEndpoint(endpoint);
        StringBuilder pathBuilder = new StringBuilder("/");

        for (int i = 0; i < segments.length; i++) {
            if (segments[i].isEmpty())
                continue;
            pathBuilder.append(segments[i]).append("/");
            String label = formatEndpointSegment(segments[i]);
            Breadcrumb breadcrumb = new Breadcrumb(label, pathBuilder.toString());
            if (isLastSegment(segments, i)) {
                breadcrumb.setLast(true);
            }
            breadcrumbs.add(breadcrumb);
        }

        return breadcrumbs;
    }

    private static boolean isLastSegment(String[] segments, int i) {
        return i == segments.length - 1;
    }

    private String[] splitEndpoint(String endpoint) {
        return endpoint.split("/");
    }

    private String formatEndpointSegment(String segment) {
        String[] segmentSplit = segment.split("-");
        for (int i = 0; i < segmentSplit.length; i++) {
            segmentSplit[i] = capitalize(segmentSplit[i]);
        }
        return String.join(" ", segmentSplit);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private Breadcrumb homeEndpoint() {
        return new Breadcrumb("Classroom", "/");
    }
}

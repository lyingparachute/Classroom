package com.example.classroom.breadcrumb;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BreadcrumbService {

    private static final String EDIT_SEGMENT = "edit";
    private static final String SEPARATOR = "/";
    private static final String HOME_LABEL = "Classroom";


    public List<Breadcrumb> getBreadcrumbs(String endpoint) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(createHomeBreadcrumb());

        String[] segments = splitEndpoint(endpoint);
        StringBuilder path = new StringBuilder();

        for (int i = 0; i < segments.length; i++) {
            path.append(segments[i]).append(SEPARATOR);
            String label = formatEndpointSegment(segments[i]);

            if (segments[i].isEmpty()) {
                continue;
            }

            if (isEditEndpoint(segments, i)) {
                // Special case for edit endpoint with ID
                String id = segments[i + 1];
                Breadcrumb breadcrumb = new Breadcrumb("Edit / " + id, path + id);
                breadcrumbs.add(breadcrumb);
                i++; // skip the ID segment
            } else {
                // Normal case for other endpoints
                Breadcrumb breadcrumb = new Breadcrumb(label, path.toString());
                if (isLastSegment(segments, i)) {
                    breadcrumb.setLast(true);
                }
                breadcrumbs.add(breadcrumb);
            }

        }

        return breadcrumbs;
    }

    private static boolean isEditEndpoint(String[] segments, int i) {
        return i < segments.length - 1 && segments[i].equals(EDIT_SEGMENT);
    }

    private static boolean isLastSegment(String[] segments, int i) {
        return i == segments.length - 1;
    }

    private String[] splitEndpoint(String endpoint) {
        return endpoint.split(SEPARATOR);
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

    private Breadcrumb createHomeBreadcrumb() {
        return new Breadcrumb(HOME_LABEL, SEPARATOR);
    }
}

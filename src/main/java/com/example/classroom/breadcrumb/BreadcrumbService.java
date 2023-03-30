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
            if (segments[i].isEmpty()) continue;

            if (i > 0) path.append(SEPARATOR);
            path.append(segments[i]);

            String label = formatEndpointSegment(segments[i]);

            if (isEditEndpoint(segments, i)) {
                addEditEndpointBreadcrumb(breadcrumbs, segments, path, i);
                i++; // skip the ID segment
            } else {
                addBreadcrumb(breadcrumbs, segments, path, i, label);
            }
        }

        return breadcrumbs;
    }

    private static void addBreadcrumb(List<Breadcrumb> breadcrumbs, String[] segments, StringBuilder path, int i, String label) {
        Breadcrumb breadcrumb = new Breadcrumb(label, path.toString());
        checkIfSegmentIsLast(segments, i, breadcrumb);
        breadcrumbs.add(breadcrumb);
    }

    private static void checkIfSegmentIsLast(String[] segments, int i, Breadcrumb breadcrumb) {
        if (i == segments.length - 1) breadcrumb.setLast(true);
    }

    private static void addEditEndpointBreadcrumb(List<Breadcrumb> breadcrumbs, String[] segments, StringBuilder path, int i) {
        String id = segments[i + 1];
        Breadcrumb breadcrumb = new Breadcrumb("Edit  /  " + id, path + SEPARATOR + id);
        checkIfSegmentIsLast(segments, ++i, breadcrumb);
        breadcrumbs.add(breadcrumb);
    }

    private static boolean isEditEndpoint(String[] segments, int i) {
        return i < segments.length - 1 && segments[i].equals(EDIT_SEGMENT);
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

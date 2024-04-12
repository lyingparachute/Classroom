package com.example.classroom.breadcrumb;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BreadcrumbService {

    private static final String EDIT_SEGMENT = "edit";
    private static final String SEPARATOR = "/";
    private static final String HOME_LABEL = "Classroom";


    public List<Breadcrumb> getBreadcrumbs(final String endpoint) {
        if (endpoint == null || endpoint.isEmpty())
            throw new IllegalArgumentException("Invalid endpoint!");

        final List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(createHomeBreadcrumb());

        final var segments = splitEndpoint(endpoint);
        final var path = new StringBuilder();

        for (int i = 0; i < segments.length; i++) {
            if (segments[i].isEmpty()) continue;

            if (i > 0) path.append(SEPARATOR);
            path.append(segments[i]);

            final var label = formatEndpointSegment(segments[i]);

            if (isEditEndpoint(segments, i)) {
                addEditEndpointBreadcrumb(breadcrumbs, segments, path, i);
                i++; // skip the ID segment
            } else {
                addBreadcrumb(breadcrumbs, segments, path, i, label);
            }
        }

        return breadcrumbs;
    }

    private static void addBreadcrumb(final List<Breadcrumb> breadcrumbs,
                                      final String[] segments,
                                      final StringBuilder path,
                                      final int i,
                                      final String label) {
        final var breadcrumb = new Breadcrumb(label, path.toString());
        checkIfSegmentIsLast(segments, i, breadcrumb);
        breadcrumbs.add(breadcrumb);
    }

    private static void checkIfSegmentIsLast(final String[] segments,
                                             final int i,
                                             final Breadcrumb breadcrumb) {
        if (i == segments.length - 1) breadcrumb.setLast(true);
    }

    private static void addEditEndpointBreadcrumb(final List<Breadcrumb> breadcrumbs,
                                                  final String[] segments,
                                                  final StringBuilder path,
                                                  int i) {
        final var id = segments[i + 1];
        final var breadcrumb = new Breadcrumb("Edit  /  " + id, path + SEPARATOR + id);
        checkIfSegmentIsLast(segments, ++i, breadcrumb);
        breadcrumbs.add(breadcrumb);
    }

    private static boolean isEditEndpoint(final String[] segments,
                                          final int i) {
        return i < segments.length - 1 && segments[i].equals(EDIT_SEGMENT);
    }

    private String[] splitEndpoint(final String endpoint) {
        return endpoint.split(SEPARATOR);
    }

    private String formatEndpointSegment(final String segment) {
        String[] segmentSplit = segment.split("-");
        for (int i = 0; i < segmentSplit.length; i++) {
            segmentSplit[i] = capitalize(segmentSplit[i]);
        }
        return String.join(" ", segmentSplit);
    }

    private String capitalize(final String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private Breadcrumb createHomeBreadcrumb() {
        return new Breadcrumb(HOME_LABEL, SEPARATOR);
    }
}

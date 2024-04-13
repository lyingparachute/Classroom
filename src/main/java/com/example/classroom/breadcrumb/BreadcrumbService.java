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

        for (int segmentNumber = 0; segmentNumber < segments.length; segmentNumber++) {
            if (segments[segmentNumber].isEmpty()) continue;

            if (segmentNumber > 0) path.append(SEPARATOR);
            path.append(segments[segmentNumber]);

            final var label = formatEndpointSegment(segments[segmentNumber]);

            if (isEditEndpoint(segments, segmentNumber)) {
                addEditEndpointBreadcrumb(breadcrumbs, segments, path, segmentNumber);
                segmentNumber++; // skip the ID segment
            } else {
                addBreadcrumb(breadcrumbs, segments, path, segmentNumber, label);
            }
        }

        return breadcrumbs;
    }

    private void addBreadcrumb(final List<Breadcrumb> breadcrumbs,
                               final String[] segments,
                               final StringBuilder path,
                               final int i,
                               final String label) {
        final var breadcrumb = new Breadcrumb(label, path.toString());
        checkIfSegmentIsLast(segments, i, breadcrumb);
        breadcrumbs.add(breadcrumb);
    }

    private void checkIfSegmentIsLast(final String[] segments,
                                      final int i,
                                      final Breadcrumb breadcrumb) {
        if (i == segments.length - 1) breadcrumb.setLast(true);
    }

    private void addEditEndpointBreadcrumb(final List<Breadcrumb> breadcrumbs,
                                           final String[] segments,
                                           final StringBuilder path,
                                           int i) {
        final var id = segments[i + 1];
        final var breadcrumb = new Breadcrumb("Edit  /  " + id, path + SEPARATOR + id);
        checkIfSegmentIsLast(segments, ++i, breadcrumb);
        breadcrumbs.add(breadcrumb);
    }

    private boolean isEditEndpoint(final String[] segments,
                                   final int i) {
        return i < segments.length - 1 && segments[i].equals(EDIT_SEGMENT);
    }

    private String[] splitEndpoint(final String endpoint) {
        return endpoint.split(SEPARATOR);
    }

    private String formatEndpointSegment(final String segment) {
        final var segmentSplit = segment.split("-");
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

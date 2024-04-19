package com.example.classroom.breadcrumb;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BreadcrumbService {

    private static final String EDIT_SEGMENT = "edit";
    private static final String SEPARATOR = "/";
    private static final String HOME_LABEL = "Classroom";

    public static Collection<Breadcrumb> getBreadcrumbs(final String endpoint) {
        if (endpoint == null || endpoint.isEmpty())
            throw new IllegalArgumentException("Invalid endpoint!");

        final List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(createHomeBreadcrumb());

        final var segments = splitEndpoint(endpoint);
        final var path = new StringBuilder();

        IntStream.range(0, segments.length).forEach(segmentNumber -> {
            final var segment = segments[segmentNumber];
            if (segment.isEmpty()) return;
            if (segmentNumber > 0) path.append(SEPARATOR);

            path.append(segment);
            final var label = formatEndpointSegment(segment);
            if (isEditEndpoint(segments, segmentNumber)) {
                addEditEndpointBreadcrumb(breadcrumbs, segments, path, segmentNumber);
            } else {
                breadcrumbs.add(new Breadcrumb(
                    label,
                    path.toString(),
                    isLastSegment(segments, segmentNumber))
                );
            }
        });
        return breadcrumbs;
    }

    private static void addEditEndpointBreadcrumb(final Collection<Breadcrumb> breadcrumbs,
                                                  final String[] segments,
                                                  final StringBuilder path,
                                                  final int segmentNumber) {
        final var id = segments[segmentNumber + 1];
        breadcrumbs.add(new Breadcrumb(
            "Edit",
            path + SEPARATOR + id,
            isLastSegment(segments, segmentNumber)));
    }

    private static boolean isLastSegment(final String[] segments,
                                         final int segmentNumber) {
        return segmentNumber == segments.length - 1;
    }

    private static boolean isEditEndpoint(final String[] segments,
                                          final int segmentNumber) {
        return segmentNumber < segments.length - 1 && segments[segmentNumber].equals(EDIT_SEGMENT);
    }

    private static String[] splitEndpoint(final String endpoint) {
        return endpoint.split(SEPARATOR);
    }

    private static String formatEndpointSegment(final String segment) {
        final var segmentSplit = segment.split("-");
        for (int i = 0; i < segmentSplit.length; i++) {
            segmentSplit[i] = capitalize(segmentSplit[i]);
        }
        return String.join(" ", segmentSplit);
    }

    private static String capitalize(final String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private static Breadcrumb createHomeBreadcrumb() {
        return new Breadcrumb(HOME_LABEL, SEPARATOR);
    }
}

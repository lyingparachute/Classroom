package com.example.classroom.breadcrumb;

public record Breadcrumb(
    String label,
    String url,
    Boolean last
) {
    Breadcrumb(final String label, final String url) {
        this(label, url, false);
    }
}

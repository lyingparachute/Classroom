package com.example.classroom.breadcrumb;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Breadcrumb {
    private String label;
    private String url;
    private boolean last;

    Breadcrumb(String label, String url) {
        this.label = label;
        this.url = url;
        this.last = false;
    }
}
